package io.aftersound.weave.service.management;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class NamespaceManager {

    interface Columns {
        String NAME = "name";
        String OWNER = "owner";
        String OWNER_EMAIL = "owner_email";
        String ATTRIBUTES = "attributes";
        String DESCRIPTION = "description";
        String CREATED = "created";
        String UPDATED = "updated";
        String TRACE = "trace";
    }

    private interface Main {
        String INSERT_SQL = "INSERT INTO namespace (name,owner,owner_email,description,attributes,created,updated,trace) VALUES(?,?,?,?,?,?,?,?)";
        String GET_SQL = "SELECT * FROM namespace WHERE name=? LIMIT 1";
        String UPDATE_SQL = "UPDATE namespace SET owner=?,owner_email=?,description=?,attributes=?,updated=?,trace=? WHERE name=?";
        String DELETE_SQL = "DELETE FROM namespace WHERE name=?";
    }

    private interface History {
        String INSERT_SQL = "INSERT INTO namespace_history (name,owner,owner_email,description,attributes,created,updated,trace) VALUES(?,?,?,?,?,?,?,?)";
    }

    private static class Record {

        private final String name;
        private final String owner;
        private final String ownerEmail;
        private final String description;
        private final String attributes;
        private final Timestamp created;
        private final Timestamp updated;
        private final String trace;

        public Record(
                String name,
                String owner,
                String ownerEmail,
                String description,
                String attributes,
                Timestamp created,
                Timestamp updated,
                String trace) {
            this.name = name;
            this.owner = owner;
            this.ownerEmail = ownerEmail;
            this.description = description;
            this.attributes = attributes;
            this.created = created;
            this.updated = updated;
            this.trace = trace;
        }

        public Namespace toLiteNamespace() {
            Namespace ns = new Namespace();
            ns.setName(name);
            ns.setOwner(owner);
            ns.setOwnerEmail(ownerEmail);
            ns.setDescription(description);
            if (attributes != null) {
                ns.setAttributes(Helper.parseAsMap(attributes));
            }
            return ns;
        }

        public Namespace toNamespace() {
            Namespace ns = new Namespace();
            ns.setName(name);
            ns.setOwner(owner);
            ns.setOwnerEmail(ownerEmail);
            ns.setDescription(description);
            if (attributes != null) {
                ns.setAttributes(Helper.parseAsMap(attributes));
            }
            if (created != null) {
                ns.setCreated(Instant.ofEpochMilli(created.getTime()));
            }
            if (updated != null) {
                ns.setUpdated(Instant.ofEpochMilli(updated.getTime()));
            }
            if (trace != null) {
                ns.setTrace(Helper.parseAsMap(trace));
            }
            return ns;
        }

    }

    private final DataSource dataSource;
    private final String user;

    public NamespaceManager(DataSource dataSource, String user) {
        this.dataSource = dataSource;
        this.user = user;
    }

    public void createNamespace(Namespace namespace) {
        Timestamp created, updated;
        created = updated = new Timestamp(System.currentTimeMillis());
        Record record = new Record(
                namespace.getName(),
                namespace.getOwner(),
                namespace.getOwnerEmail(),
                namespace.getDescription(),
                Helper.toJson(namespace.getAttributes()),
                created,
                updated,
                Helper.insertedByTrace(user)
        );

        insertRecord(record);
        insertHistoryRecord(record);
    }

    public Namespace getNamespace(String name) {
        Record record = getRecord(name);
        return record != null ? record.toLiteNamespace() : null;
    }

    public boolean hasNamespace(String name) {
        return getRecord(name) != null;
    }

    public void updateNamespace(Namespace namespace) {
        Record record = getRecord(namespace.getName());

        Record updatedRecord = new Record(
                namespace.getName(),
                namespace.getOwner(),
                namespace.getOwnerEmail(),
                namespace.getDescription(),
                Helper.toJson(namespace.getAttributes()),
                record.created,
                new Timestamp(System.currentTimeMillis()),
                Helper.updatedByTrace(user)
        );

        updateRecord(updatedRecord);
        insertHistoryRecord(updatedRecord);
    }

    public void deleteNamespace(String name) {
        Record record = getRecord(name);

        Record deletedRecord = new Record(
                record.name,
                record.owner,
                record.ownerEmail,
                record.description,
                record.attributes,
                record.created,
                new Timestamp(System.currentTimeMillis()),
                Helper.deletedByTrace(user)
        );
        insertHistoryRecord(deletedRecord);

        deleteRecord(name);

    }

    public List<Namespace> findNamespaces(Map<String, String> filters, int fetchCount, int skipCount) {
        List<String> conditions = new ArrayList<>(filters.size());
        for (Map.Entry<String, String> e : filters.entrySet()) {
            conditions.add(String.format("%s LIKE '%%%s%%'", e.getKey(), e.getValue()));
        }

        String whereClause = "";
        if (conditions.size() > 0) {
            StringJoiner stringJoiner = new StringJoiner(" AND ");
            for (String condition : conditions) {
                stringJoiner.add(condition);
            }
            whereClause = "WHERE " + stringJoiner + " ";
        }

        final String sql = String.format(
                "SELECT * FROM namespace %s LIMIT %d OFFSET %d",
                whereClause,
                fetchCount,
                skipCount
        );

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            List<Namespace> namespaces = new ArrayList<>();
            while (rs.next()) {
                namespaces.add(parseRecord(rs).toLiteNamespace());
            }
            return namespaces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Namespace> findNamespaceHistory(Map<String, String> filters, int fetchCount, int skipCount) {
        List<String> conditions = new ArrayList<>(filters.size());
        for (Map.Entry<String, String> e : filters.entrySet()) {
            conditions.add(String.format("%s LIKE '%%%s%%'", e.getKey(), e.getValue()));
        }

        String whereClause = "";
        if (conditions.size() > 0) {
            StringJoiner stringJoiner = new StringJoiner(" AND ");
            for (String condition : conditions) {
                stringJoiner.add(condition);
            }
            whereClause = "WHERE " + stringJoiner + " ";
        }

        final String sql = String.format(
                "SELECT * FROM namespace_history %s ORDER BY updated ASC LIMIT %d OFFSET %d",
                whereClause,
                fetchCount,
                skipCount
        );

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            List<Namespace> namespaces = new ArrayList<>();
            while (rs.next()) {
                namespaces.add(parseRecord(rs).toNamespace());
            }
            return namespaces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertRecord(Record record) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Main.INSERT_SQL)) {
            
            preparedStatement.setString(1, record.name);
            preparedStatement.setString(2, record.owner);
            preparedStatement.setString(3, record.ownerEmail);
            preparedStatement.setString(4, record.description);
            preparedStatement.setString(5, record.attributes);
            preparedStatement.setTimestamp(6, record.created);
            preparedStatement.setTimestamp(7, record.updated);
            preparedStatement.setString(8, record.trace);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Record getRecord(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Main.GET_SQL)) {
            
            preparedStatement.setString(1, name);

            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return parseRecord(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateRecord(Record record) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Main.UPDATE_SQL)) {

            preparedStatement.setString(1, record.owner);
            preparedStatement.setString(2, record.ownerEmail);
            preparedStatement.setString(3, record.description);
            preparedStatement.setString(4, record.attributes);
            preparedStatement.setTimestamp(5, record.updated);
            preparedStatement.setString(6, record.trace);
            preparedStatement.setString(7, record.name);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteRecord(String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Main.DELETE_SQL)) {
            preparedStatement.setString(1, name);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertHistoryRecord(Record record) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(History.INSERT_SQL)) {

            preparedStatement.setString(1, record.name);
            preparedStatement.setString(2, record.owner);
            preparedStatement.setString(3, record.ownerEmail);
            preparedStatement.setString(4, record.description);
            preparedStatement.setString(5, record.attributes);
            preparedStatement.setTimestamp(6, record.created);
            preparedStatement.setTimestamp(7, record.updated);
            preparedStatement.setString(8, record.trace);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Record parseRecord(ResultSet rs) throws SQLException {
        return new Record(
                rs.getString(Columns.NAME),
                rs.getString(Columns.OWNER),
                rs.getString(Columns.OWNER_EMAIL),
                rs.getString(Columns.DESCRIPTION),
                rs.getString(Columns.ATTRIBUTES),
                rs.getTimestamp(Columns.CREATED),
                rs.getTimestamp(Columns.UPDATED),
                rs.getString(Columns.TRACE)
        );
    }
    
}
