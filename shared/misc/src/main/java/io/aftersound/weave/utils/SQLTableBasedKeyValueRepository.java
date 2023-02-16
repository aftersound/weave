package io.aftersound.weave.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

/**
 * An implementation of {@link KeyValueRepository} based on SQL table
 */
public class SQLTableBasedKeyValueRepository implements KeyValueRepository {

    private static class Main {
        static final String INSERT_SQL_TEMPLATE = "INSERT INTO $TABLE$ (k,v,created,updated,trace) VALUES(?,?,?,?,?)";
        static final String GET_SQL_TEMPLATE = "SELECT * FROM $TABLE$ WHERE k=?";
        static final String UPDATE_SQL_TEMPLATE = "UPDATE $TABLE$ SET v=?, updated=?, trace=? WHERE k=?";
        static final String DELETE_SQL_TEMPLATE = "DELETE FROM $TABLE$ WHERE k=?";
        static final String GET_RECORDS_TEMPLATE = "SELECT * FROM $TABLE$ WHERE k in (KEYS)";
    }

    private static class History {
        static final String INSERT_SQL_TEMPLATE = "INSERT INTO $TABLE$_history (k,v,created,updated,trace) VALUES(?,?,?,?,?)";
    }

    private static class Columns {
        static final String KEY = "k";
        static final String VALUE = "v";
        static final String CREATED = "created";
        static final String UPDATED = "updated";
        static final String TRACE = "trace";
    }

    private static class Record {
        final String key;
        final byte[] value;
        final Timestamp created;
        final Timestamp updated;
        final String trace;

        public Record(String key, byte[] value, Timestamp created, Timestamp updated, String trace) {
            this.key = key;
            this.value = value;
            this.created = created;
            this.updated = updated;
            this.trace = trace;
        }
    }

    private static final String TABLE = "$TABLE$";

    private static final String INSERT_TRACE = "{\"op\":\"insert\",\"by\":\"USER\"}";
    private static final String UPDATE_TRACE = "{\"op\":\"update\",\"by\":\"USER\"}";
    private static final String DELETE_TRACE = "{\"op\":\"delete\",\"by\":\"USER\"}";

    private final Connection connection;
    private final String insertSql;
    private final String getSql;
    private final String updateSql;
    private final String deleteSql;
    private final String getRecordsSql;

    private final String operatedBy;
    private final String historyInsertSql;

    private SQLTableBasedKeyValueRepository(Connection connection, String tableName, boolean historyRequired, String operatedBy) {
        this.connection = connection;
        this.operatedBy = operatedBy;

        this.insertSql = Main.INSERT_SQL_TEMPLATE.replace(TABLE, tableName);
        this.getSql = Main.GET_SQL_TEMPLATE.replace(TABLE, tableName);
        this.updateSql = Main.UPDATE_SQL_TEMPLATE.replace(TABLE, tableName);
        this.deleteSql = Main.DELETE_SQL_TEMPLATE.replace(TABLE, tableName);
        this.getRecordsSql = Main.GET_RECORDS_TEMPLATE.replace(TABLE, tableName);

        if (historyRequired) {
            this.historyInsertSql = History.INSERT_SQL_TEMPLATE.replace(TABLE, tableName);
        } else {
            this.historyInsertSql = null;
        }
    }

    public static SQLTableBasedKeyValueRepository create(Connection connection, String table) {
        return new SQLTableBasedKeyValueRepository(connection, table, false, null);
    }

    public static SQLTableBasedKeyValueRepository create(Connection connection, String table, String operatedBy) {
        if (operatedBy == null) {
            throw new IllegalArgumentException("operatedBy cannot be null");
        }
        return new SQLTableBasedKeyValueRepository(connection, table, false, operatedBy);
    }

    public static SQLTableBasedKeyValueRepository createWithHistory(Connection connection, String table, String operatedBy) {
        if (operatedBy == null) {
            throw new IllegalArgumentException("operatedBy cannot be null");
        }
        return new SQLTableBasedKeyValueRepository(connection, table, true, operatedBy);
    }

    @Override
    public void insert(String key, byte[] value) throws Exception {
        // create new record
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        final String trace = INSERT_TRACE.replace("USER", operatedBy != null ? operatedBy : "unknown");
        final Record r = new Record(key, value, now, now, trace);

        // insert into db
        insertRecord(r, insertSql);

        recordInHistory(r);
    }

    @Override
    public byte[] get(String key) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSql)) {
            preparedStatement.setString(1, key);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBytes(Columns.VALUE);
            } else {
                return null;
            }
        }
    }

    @Override
    public void update(String key, byte[] value) throws Exception {
        // get existing record
        Record r = getRecord(key);
        if (r == null) {
            return;
        }

        // create updated record
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        final String trace = UPDATE_TRACE.replace("USER", operatedBy != null ? operatedBy : "unknown");
        Record u = new Record(r.key, value, r.created, now, trace);

        // update db record
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setBytes(1, u.value);
            preparedStatement.setTimestamp(2, u.updated);
            preparedStatement.setString(3, u.trace);
            preparedStatement.setString(4, u.key);
            preparedStatement.execute();
        }

        // record in history
        recordInHistory(u);
    }

    @Override
    public void delete(String key) throws Exception {
        // get existing record
        Record r = getRecord(key);
        if (r == null) {
            return;
        }

        // create deleted record
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        final String trace = DELETE_TRACE.replace("USER", operatedBy != null ? operatedBy : "unknown");
        Record d = new Record(r.key, r.value, r.created, now, trace);

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            preparedStatement.setString(1, d.key);
            preparedStatement.execute();
        }

        // record in history
        recordInHistory(d);
    }

    @Override
    public void bulkInsert(Map<String, byte[]> kvs) throws Exception {
        // create records
        final List<Record> records = new ArrayList<>(kvs.size());
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        final String trace = INSERT_TRACE.replace("USER", operatedBy != null ? operatedBy : "unknown");
        for (Map.Entry<String, byte[]> e : kvs.entrySet()) {
            records.add(
                    new Record(e.getKey(), e.getValue(), now, now, trace)
            );
        }

        // insert into db
        insertRecords(records, insertSql);

        // record in history
        recordInHistory(records);
    }

    @Override
    public Map<String, byte[]> bulkGet(Collection<String> keys) throws Exception {
        Map<String, Record> records = getRecords(keys);
        Map<String, byte[]> kvm = new LinkedHashMap<>(keys.size());
        for (String key : keys) {
            Record r = records.get(key);
            if (r != null) {
                kvm.put(key, r.value);
            }
        }
        return kvm;
    }

    @Override
    public void bulkUpdate(Map<String, byte[]> kvs) throws Exception {
        // get existing records
        final Map<String, Record> records = getRecords(kvs.keySet());
        if (records.isEmpty()) {
            return;
        }

        // create updated records
        final List<Record> updatedRecords = new ArrayList<>(kvs.size());
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        final String trace = UPDATE_TRACE.replace("USER", operatedBy != null ? operatedBy : "unknown");
        for (Map.Entry<String, Record> e : records.entrySet()) {
            Record r = e.getValue();
            updatedRecords.add(
                    new Record(r.key, kvs.get(r.key), r.created, now, trace)
            );
        }

        // update db
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            for (Record u : updatedRecords) {
                preparedStatement.setBytes(1, u.value);
                preparedStatement.setTimestamp(2, u.updated);
                preparedStatement.setString(3, u.trace);
                preparedStatement.setString(4, u.key);

                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        }

        // record in history
        recordInHistory(updatedRecords);
    }

    @Override
    public void bulkDelete(Collection<String> keys) throws Exception {
        // get existing records
        final Map<String, Record> records = getRecords(keys);
        if (records.isEmpty()) {
            return;
        }

        // create deleted records
        final List<Record> deletedRecords = new ArrayList<>(keys.size());
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        final String trace = DELETE_TRACE.replace("USER", operatedBy != null ? operatedBy : "unknown");
        for (Map.Entry<String, Record> e : records.entrySet()) {
            Record r = e.getValue();
            deletedRecords.add(
                    new Record(r.key, r.value, r.created, now, trace)
            );
        }

        // delete from db
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteSql)) {
            for (Record d : deletedRecords) {
                preparedStatement.setString(1, d.key);

                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        }

        // record in history
        recordInHistory(deletedRecords);
    }

    private Record getRecord(String key) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement(getSql)) {
            preparedStatement.setString(1, key);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return parseRecord(rs);
            } else {
                return null;
            }
        }
    }

    private Record parseRecord(ResultSet rs) throws Exception {
        return new Record(
                rs.getString(Columns.KEY),
                rs.getBytes(Columns.VALUE),
                rs.getTimestamp(Columns.CREATED),
                rs.getTimestamp(Columns.UPDATED),
                rs.getString(Columns.TRACE)
        );
    }

    private void recordInHistory(Record r) throws Exception {
        if (historyInsertSql != null) {
            insertRecord(r, historyInsertSql);
        }
    }

    private void insertRecord(Record r, String insertSql) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            preparedStatement.setString(1, r.key);
            preparedStatement.setBytes(2, r.value);
            preparedStatement.setTimestamp(3, r.created);
            preparedStatement.setTimestamp(4, r.updated);
            preparedStatement.setString(5, r.trace);
            preparedStatement.execute();
        }
    }

    private Map<String, Record> getRecords(Collection<String> keys) throws Exception {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if (!first) {
                sb.append(",");
            } else {
                first = false;
            }
            sb.append('\'').append(key).append('\'');
        }
        final String sql = getRecordsSql.replace("KEYS", sb.toString());
        Map<String, Record> records = new HashMap<>(keys.size());
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Record r = parseRecord(rs);
                records.put(r.key, r);
            }
        }
        return records;
    }

    private void recordInHistory(List<Record> records) throws Exception {
        if (historyInsertSql != null) {
            insertRecords(records, historyInsertSql);
        }
    }

    private void insertRecords(List<Record> records, String insertSql) throws Exception {
        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            for (Record r : records) {
                preparedStatement.setString(1, r.key);
                preparedStatement.setBytes(2, r.value);
                preparedStatement.setTimestamp(3, r.created);
                preparedStatement.setTimestamp(4, r.updated);
                preparedStatement.setString(5, r.trace);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
        }
    }
}
