package io.aftersound.weave.service.management;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class InstanceManager {

    interface Columns {
        String HOST = "host";
        String PORT = "port";
        String IPV4_ADDRESS = "ipv4_address";
        String IPV6_ADDRESS = "ipv6_address";
        String NAMESPACE = "namespace";
        String APPLICATION = "application";
        String ENVIRONMENT = "environment";
        String STATUS = "status";
        String UPDATED = "updated";
    }

    private interface SQL {
        String UPSERT =
                "MERGE INTO instance AS i USING (VALUES (?,?,?,?,?,?,?,?,?)) AS r(host,port,ipv4_address,ipv6_address,namespace,application,environment,status,updated) " +
                "ON (i.host=r.host AND i.port=r.port) " +
                "WHEN MATCHED THEN UPDATE SET i.ipv4_address=r.ipv4_address,i.ipv6_address=r.ipv6_address,i.namespace=r.namespace,i.application=r.application,i.environment=r.environment,i.status=r.status,i.updated=r.updated " +
                "WHEN NOT MATCHED THEN INSERT (host,port,ipv4_address,ipv6_address,namespace,application,environment,status,updated) VALUES (r.host,r.port,r.ipv4_address,r.ipv6_address,r.namespace,r.application,r.environment,r.status,r.updated)";
        String GET_STATUS = "SELECT status FROM instance WHERE host=? AND port=? AND namespace=? AND application=?";
        String UPDATE_STATUS = "UPDATE instance SET status=?,updated=? WHERE host=? AND port=? AND namespace=? AND application=?";
        String DELETE = "DELETE FROM instance WHERE host=? AND port=? AND namespace=? AND application=?";
    }

    private static class Record {

        private final String host;
        private final int port;
        private final String ipv4Address;
        private final String ipv6Address;
        private final String namespace;
        private final String application;
        private final String environment;
        private final String status;
        private final Timestamp updated;

        public Record(
                String host,
                int port,
                String namespace,
                String application,
                String environment,
                String ipv4Address,
                String ipv6Address,
                String status,
                Timestamp updated) {
            this.host = host;
            this.port = port;
            this.namespace = namespace;
            this.application = application;
            this.environment = environment;
            this.ipv4Address = ipv4Address;
            this.ipv6Address = ipv6Address;
            this.status = status;
            this.updated = updated;
        }

        public Instance toInstance() {
            Instance instance = new Instance();
            instance.setHost(host);
            instance.setPort(port);
            instance.setNamespace(namespace);
            instance.setApplication(application);
            instance.setEnvironment(environment);
            instance.setIpv4Address(ipv4Address);
            instance.setIpv6Address(ipv6Address);
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
                instance.getHost(),
                instance.getPort(),
                instance.getNamespace(),
                instance.getApplication(),
                instance.getEnvironment(),
                instance.getIpv4Address(),
                instance.getIpv6Address(),
                "registered",
                updated
        );

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(SQL.UPSERT)) {
                preparedStatement.setString(1, record.host);
                preparedStatement.setInt(2, record.port);
                preparedStatement.setString(3, record.ipv4Address);
                preparedStatement.setString(4, record.ipv6Address);
                preparedStatement.setString(5, record.namespace);
                preparedStatement.setString(6, record.application);
                preparedStatement.setString(7, record.environment);
                preparedStatement.setString(8, record.status);
                preparedStatement.setTimestamp(9, record.updated);

                preparedStatement.execute();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void unregisterInstance(Instance instance) {
        Timestamp updated = new Timestamp(System.currentTimeMillis());
        Record record = new Record(
                instance.getHost(),
                instance.getPort(),
                instance.getNamespace(),
                instance.getApplication(),
                instance.getEnvironment(),
                instance.getIpv4Address(),
                instance.getIpv6Address(),
                "unregistered",
                updated
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.DELETE)) {
            preparedStatement.setString(1, record.host);
            preparedStatement.setInt(2, record.port);
            preparedStatement.setString(3, record.namespace);
            preparedStatement.setString(4, record.application);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void markup(Instance instance) {
        Timestamp updated = new Timestamp(System.currentTimeMillis());
        Record record = new Record(
                instance.getHost(),
                instance.getPort(),
                instance.getNamespace(),
                instance.getApplication(),
                instance.getEnvironment(),
                instance.getIpv4Address(),
                instance.getIpv6Address(),
                "up",
                updated
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.UPDATE_STATUS)) {
            preparedStatement.setString(1, record.status);
            preparedStatement.setTimestamp(2, record.updated);
            preparedStatement.setString(3, record.host);
            preparedStatement.setInt(4, record.port);
            preparedStatement.setString(5, record.namespace);
            preparedStatement.setString(6, record.application);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void markdown(Instance instance) {
        Timestamp updated = new Timestamp(System.currentTimeMillis());
        Record record = new Record(
                instance.getHost(),
                instance.getPort(),
                instance.getNamespace(),
                instance.getApplication(),
                instance.getEnvironment(),
                instance.getIpv4Address(),
                instance.getIpv6Address(),
                "down",
                updated
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.UPDATE_STATUS)) {
            preparedStatement.setString(1, record.status);
            preparedStatement.setTimestamp(2, record.updated);
            preparedStatement.setString(3, record.host);
            preparedStatement.setInt(4, record.port);
            preparedStatement.setString(5, record.namespace);
            preparedStatement.setString(6, record.application);

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
                "SELECT * FROM instance %s LIMIT %d OFFSET %d",
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
                rs.getString(Columns.HOST),
                rs.getInt(Columns.PORT),
                rs.getString(Columns.NAMESPACE),
                rs.getString(Columns.APPLICATION),
                rs.getString(Columns.ENVIRONMENT),
                rs.getString(Columns.IPV4_ADDRESS),
                rs.getString(Columns.IPV6_ADDRESS),
                rs.getString(Columns.STATUS),
                rs.getTimestamp(Columns.UPDATED)
        );
    }

}
