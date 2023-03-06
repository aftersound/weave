package io.aftersound.weave.service.management;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class InstanceManager {

    interface Columns {
        String ID = "iid";
        String NAMESPACE = "namespace";
        String APPLICATION = "application";
        String ENVIRONMENT = "environment";
        String HOST = "host";
        String PORT = "port";
        String IPV4_ADDRESS = "ipv4_address";
        String IPV6_ADDRESS = "ipv6_address";
        String STATUS = "status";
        String UPDATED = "updated";
    }

    private interface SQL {
        String UPSERT =
                "MERGE INTO instance AS i USING (VALUES (?,?,?,?,?,?,?,?,?,?)) AS r(iid,namespace,application,environment,host,port,ipv4_address,ipv6_address,status,updated) " +
                "ON (i.iid=r.iid) " +
                "WHEN MATCHED THEN UPDATE SET i.namespace=r.namespace,i.application=r.application,i.environment=r.environment,i.host=r.host,i.port=r.port,i.ipv4_address=r.ipv4_address,i.ipv6_address=r.ipv6_address,i.status=r.status,i.updated=r.updated " +
                "WHEN NOT MATCHED THEN INSERT (iid,namespace,application,environment,host,port,ipv4_address,ipv6_address,status,updated) VALUES (r.iid,r.namespace,r.application,r.environment,r.host,r.port,r.ipv4_address,r.ipv6_address,r.status,r.updated)";
        String GET_STATUS = "SELECT status FROM instance WHERE iid=? AND host=? AND port=? AND namespace=? AND application=?";
        String UPDATE_STATUS = "UPDATE instance SET status=?,updated=? WHERE iid=? AND host=? AND port=? AND namespace=? AND application=?";
        String DELETE = "DELETE FROM instance WHERE iid=? AND host=? AND port=? AND namespace=? AND application=?";
    }

    private static class Record {
        private final String id;
        private final String namespace;
        private final String application;
        private final String environment;
        private final String host;
        private final int port;
        private final String ipv4Address;
        private final String ipv6Address;
        private final String status;
        private final Timestamp updated;

        public Record(
                String id,
                String namespace,
                String application,
                String environment,
                String host,
                int port,
                String ipv4Address,
                String ipv6Address,
                String status,
                Timestamp updated) {
            this.id = id;
            this.namespace = namespace;
            this.application = application;
            this.environment = environment;
            this.host = host;
            this.port = port;
            this.ipv4Address = ipv4Address;
            this.ipv6Address = ipv6Address;
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
                instance.getId(),
                instance.getNamespace(),
                instance.getApplication(),
                instance.getEnvironment(),
                instance.getHost(),
                instance.getPort(),
                instance.getIpv4Address(),
                instance.getIpv6Address(),
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
                preparedStatement.setString(7, record.ipv4Address);
                preparedStatement.setString(8, record.ipv6Address);
                preparedStatement.setString(9, record.status);
                preparedStatement.setTimestamp(10, record.updated);

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
                instance.getIpv4Address(),
                instance.getIpv6Address(),
                "unregistered",
                updated
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.DELETE)) {
            preparedStatement.setString(1, record.id);
            preparedStatement.setString(2, record.host);
            preparedStatement.setInt(3, record.port);
            preparedStatement.setString(4, record.namespace);
            preparedStatement.setString(5, record.application);

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
                instance.getIpv4Address(),
                instance.getIpv6Address(),
                "up",
                updated
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.UPDATE_STATUS)) {
            preparedStatement.setString(1, record.status);
            preparedStatement.setTimestamp(2, record.updated);
            preparedStatement.setString(3, record.id);
            preparedStatement.setString(4, record.host);
            preparedStatement.setInt(5, record.port);
            preparedStatement.setString(6, record.namespace);
            preparedStatement.setString(7, record.application);

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
                instance.getIpv4Address(),
                instance.getIpv6Address(),
                "down",
                updated
        );

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL.UPDATE_STATUS)) {
            preparedStatement.setString(1, record.status);
            preparedStatement.setTimestamp(2, record.updated);
            preparedStatement.setString(3, record.id);
            preparedStatement.setString(4, record.host);
            preparedStatement.setInt(5, record.port);
            preparedStatement.setString(6, record.namespace);
            preparedStatement.setString(7, record.application);

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
                rs.getString(Columns.ID),
                rs.getString(Columns.NAMESPACE),
                rs.getString(Columns.APPLICATION),
                rs.getString(Columns.ENVIRONMENT),
                rs.getString(Columns.HOST),
                rs.getInt(Columns.PORT),
                rs.getString(Columns.IPV4_ADDRESS),
                rs.getString(Columns.IPV6_ADDRESS),
                rs.getString(Columns.STATUS),
                rs.getTimestamp(Columns.UPDATED)
        );
    }

}
