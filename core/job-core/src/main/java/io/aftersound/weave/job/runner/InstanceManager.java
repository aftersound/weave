package io.aftersound.weave.job.runner;

import io.aftersound.weave.job.Helper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class InstanceManager {

    public interface Columns {
        String ID = "iid";
        String NAMESPACE = "namespace";
        String APPLICATION = "application";
        String ENVIRONMENT = "environment";
        String HOST = "host";
        String PORT = "port";
        String LABELS = "labels";
        String STATUS = "status";
        String UPDATED = "updated";
    }

    private interface SQL {
        String UPSERT =
                "INSERT INTO runner_instance (iid,namespace,application,environment,host,port,labels,status,updated) " +
                        "VALUES (?,?,?,?,?,?,?,?,?) " +
                        "ON DUPLICATE KEY UPDATE namespace=?,application=?,environment=?,host=?,port=?,labels=?,status=?,updated=?";
        String GET_STATUS = "SELECT status FROM runner_instance WHERE iid=?";
        String UPDATE_STATUS = "UPDATE runner_instance SET status=?,updated=? WHERE iid=?";
        String DELETE = "DELETE FROM runner_instance WHERE iid=?";
    }

    private static class Record {

        private final String id;
        private final String namespace;
        private final String application;
        private final String environment;
        private final String host;
        private final int port;
        private final String labels;
        private final String status;
        private final Timestamp updated;

        public Record(
                String id,
                String namespace,
                String application,
                String environment,
                String host,
                int port,
                String labels,
                String status,
                Timestamp updated) {
            this.id = id;
            this.namespace = namespace;
            this.application = application;
            this.environment = environment;
            this.host = host;
            this.port = port;
            this.labels = labels;
            this.status = status;
            this.updated = updated;
        }

        public Instance toInstance() {
            Instance instance = new Instance();
            instance.setId(id);
            instance.setNamespace(namespace);
            instance.setApplication(application);
            instance.setEnvironment(environment);
            instance.setHost(host);
            instance.setPort(port);
            instance.setLabels(Helper.parseAsMap(labels));
            instance.setStatus(status);
            instance.setUpdated(new Date(updated.getTime()));
            return instance;
        }

    }

    private final DataSource dataSource;
    private final String user;

    public InstanceManager(DataSource dataSource, String user) {
        this.dataSource = dataSource;
        this.user = user;
    }

    public void registerInstance(Instance instance) {
        Timestamp updated = new Timestamp(System.currentTimeMillis());
        Record record = new Record(
                instance.getId(),
                instance.getNamespace(),
                instance.getApplication(),
                instance.getEnvironment(),
                instance.getHost(),
                instance.getPort(),
                Helper.toJson(instance.getLabels()),
                "registered",
                updated
        );

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL.UPSERT)) {
                preparedStatement.setString(1, record.id);
                preparedStatement.setString(2, record.namespace);
                preparedStatement.setString(3, record.application);
                preparedStatement.setString(4, record.environment);
                preparedStatement.setString(5, record.host);
                preparedStatement.setInt(6, record.port);
                preparedStatement.setString(7, record.labels);
                preparedStatement.setString(8, record.status);
                preparedStatement.setTimestamp(9, record.updated);
                preparedStatement.setString(10, record.namespace);
                preparedStatement.setString(11, record.application);
                preparedStatement.setString(12, record.environment);
                preparedStatement.setString(13, record.host);
                preparedStatement.setInt(14, record.port);
                preparedStatement.setString(15, record.labels);
                preparedStatement.setString(16, record.status);
                preparedStatement.setTimestamp(17, record.updated);

                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregisterInstance(Instance instance) {
        Timestamp updated = new Timestamp(System.currentTimeMillis());
        Record record = new Record(
                instance.getId(),
                instance.getNamespace(),
                instance.getApplication(),
                instance.getEnvironment(),
                instance.getHost(),
                instance.getPort(),
                Helper.toJson(instance.getLabels()),
                "unregistered",
                updated
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.DELETE)) {
            preparedStatement.setString(1, record.id);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void markup(Instance instance) {
        Timestamp updated = new Timestamp(System.currentTimeMillis());
        Record record = new Record(
                instance.getId(),
                instance.getNamespace(),
                instance.getApplication(),
                instance.getEnvironment(),
                instance.getHost(),
                instance.getPort(),
                Helper.toJson(instance.getLabels()),
                "up",
                updated
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.UPDATE_STATUS)) {
            preparedStatement.setString(1, record.status);
            preparedStatement.setTimestamp(2, record.updated);
            preparedStatement.setString(3, record.id);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void markdown(Instance instance) {
        Timestamp updated = new Timestamp(System.currentTimeMillis());
        Record record = new Record(
                instance.getId(),
                instance.getNamespace(),
                instance.getApplication(),
                instance.getEnvironment(),
                instance.getHost(),
                instance.getPort(),
                Helper.toJson(instance.getLabels()),
                "down",
                updated
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.UPDATE_STATUS)) {
            preparedStatement.setString(1, record.status);
            preparedStatement.setTimestamp(2, record.updated);
            preparedStatement.setString(3, record.id);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Instance> findInstances(Map<String, String> filters, int fetchCount, int skipCount) {
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
                "SELECT * FROM runner_instance %s LIMIT %d OFFSET %d",
                whereClause,
                fetchCount,
                skipCount
        );

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            List<Instance> instances = new ArrayList<>();
            while (rs.next()) {
                instances.add(parseRecord(rs).toInstance());
            }
            return instances;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Instance> findInstancesByLabels(Map<String, String> labels, int fetchCount, int skipCount) {
        List<String> conditions = new ArrayList<>(labels.size());
        for (Map.Entry<String, String> e : labels.entrySet()) {
            conditions.add(String.format("JSON_EXTRACT(labels,$.%s)='%s'", e.getKey(), e.getValue()));
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
                "SELECT * FROM runner_instance %s LIMIT %d OFFSET %d",
                whereClause,
                fetchCount,
                skipCount
        );

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            List<Instance> instances = new ArrayList<>();
            while (rs.next()) {
                instances.add(parseRecord(rs).toInstance());
            }
            return instances;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Record parseRecord(ResultSet rs) throws SQLException {
        return new Record(
                rs.getString(Columns.ID),
                rs.getString(Columns.NAMESPACE),
                rs.getString(Columns.APPLICATION),
                rs.getString(Columns.ENVIRONMENT),
                rs.getString(Columns.HOST),
                rs.getInt(Columns.PORT),
                rs.getString(Columns.LABELS),
                rs.getString(Columns.STATUS),
                rs.getTimestamp(Columns.UPDATED)
        );
    }

}
