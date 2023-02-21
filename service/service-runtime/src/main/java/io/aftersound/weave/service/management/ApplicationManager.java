package io.aftersound.weave.service.management;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class ApplicationManager {

    private interface Columns {
        String NAMESPACE = "namespace";
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
        String INSERT_SQL = "INSERT INTO application (namespace,name,owner,owner_email,description,attributes,created,updated,trace) VALUES(?,?,?,?,?,?,?,?,?)";
        String GET_SQL = "SELECT * FROM application WHERE namespace=? AND name=? LIMIT 1";
        String UPDATE_SQL = "UPDATE application SET owner=?,owner_email=?,description=?,attributes=?,updated=?,trace=? WHERE namespace=? AND name=?";
        String DELETE_SQL = "DELETE FROM application WHERE namespace=? AND name=?";
    }

    private interface History {
        String INSERT_SQL = "INSERT INTO application_history (namespace,name,owner,owner_email,description,attributes,created,updated,trace) VALUES(?,?,?,?,?,?,?,?,?)";
    }

    private static class Record {

        private final String namespace;
        private final String name;
        private final String owner;
        private final String ownerEmail;
        private final String description;
        private final String attributes;
        private final Timestamp created;
        private final Timestamp updated;
        private final String trace;

        public Record(
                String namespace,
                String name,
                String owner,
                String ownerEmail,
                String description,
                String attributes,
                Timestamp created,
                Timestamp updated,
                String trace) {
            this.namespace = namespace;
            this.name = name;
            this.owner = owner;
            this.ownerEmail = ownerEmail;
            this.description = description;
            this.attributes = attributes;
            this.created = created;
            this.updated = updated;
            this.trace = trace;
        }

        public Application toApplication() {
            Application app = new Application();
            app.setNamespace(namespace);
            app.setName(name);
            app.setOwner(owner);
            app.setOwnerEmail(ownerEmail);
            app.setDescription(description);
            app.setAttributes(Helper.parseAsMap(attributes));
            return app;
        }
    }

    private final DataSource dataSource;
    private final String user;

    private final NamespaceManager namespaceManager;

    public ApplicationManager(DataSource dataSource, String user) {
        this.dataSource = dataSource;
        this.user = user;

        this.namespaceManager = new NamespaceManager(dataSource, user);
    }

    public void createApplication(Application application) throws NoSuchNamespaceException {
        Timestamp created, updated;
        created = updated = new Timestamp(System.currentTimeMillis());
        Record record = new Record(
                application.getNamespace(),
                application.getName(),
                application.getOwner(),
                application.getOwnerEmail(),
                application.getDescription(),
                Helper.toJson(application.getAttributes()),
                created,
                updated,
                Helper.insertedByTrace(user)
        );

        insertRecord(record);
        insertHistoryRecord(record);
    }

    public Application getApplication(String namespace, String name) {
        Record record = getRecord(namespace, name);
        return record != null ? record.toApplication() : null;
    }

    public boolean hasApplication(String namespace, String name) {
        return getRecord(namespace, name) != null;
    }

    public void updateApplication(Application application) {
        Record record = getRecord(application.getNamespace(), application.getName());

        Record updatedRecord = new Record(
                record.namespace,
                record.name,
                application.getOwner(),
                application.getOwnerEmail(),
                application.getDescription(),
                Helper.toJson(application.getAttributes()),
                record.created,
                new Timestamp(System.currentTimeMillis()),
                Helper.updatedByTrace(user)
        );

        updateRecord(updatedRecord);
        insertHistoryRecord(updatedRecord);
    }

    public void deleteApplication(String namespace, String name) {
        deleteRecord(namespace, name);
    }

    public List<Application> findApplications(Map<String, String> filters, int fetchCount, int skipCount) {
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
                "SELECT * FROM application %s LIMIT %d OFFSET %d",
                whereClause,
                fetchCount,
                skipCount
        );

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            List<Application> applications = new ArrayList<>();
            while (rs.next()) {
                applications.add(parseRecord(rs).toApplication());
            }
            return applications;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Application> findApplicationHistory(Map<String, String> filters, int fetchCount, int skipCount) {
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
                "SELECT * FROM application_history ORDER BY updated DESC %s LIMIT %d OFFSET %d",
                whereClause,
                fetchCount,
                skipCount
        );

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(sql);
            List<Application> applications = new ArrayList<>();
            while (rs.next()) {
                applications.add(parseRecord(rs).toApplication());
            }
            return applications;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Application parseApplication(ResultSet rs) throws SQLException {
        Application app = new Application();
        app.setNamespace(rs.getString(Columns.NAMESPACE));
        app.setName(rs.getString(Columns.NAME));
        app.setOwner(rs.getString(Columns.OWNER));
        app.setOwnerEmail(rs.getString(Columns.OWNER_EMAIL));
        app.setDescription(rs.getString(Columns.DESCRIPTION));
        app.setAttributes(Helper.parseAsMap(rs.getString(Columns.ATTRIBUTES)));
        return app;
    }

    private void insertRecord(Record record) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Main.INSERT_SQL)) {

            preparedStatement.setString(1, record.namespace);
            preparedStatement.setString(2, record.name);
            preparedStatement.setString(3, record.owner);
            preparedStatement.setString(4, record.ownerEmail);
            preparedStatement.setString(5, record.description);
            preparedStatement.setString(6, record.attributes);
            preparedStatement.setTimestamp(7, record.created);
            preparedStatement.setTimestamp(8, record.updated);
            preparedStatement.setString(9, record.trace);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Record getRecord(String namespace, String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Main.GET_SQL)) {

            preparedStatement.setString(1, namespace);
            preparedStatement.setString(2, name);

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
            preparedStatement.setString(7, record.namespace);
            preparedStatement.setString(8, record.name);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteRecord(String namespace, String name) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(Main.DELETE_SQL)) {
            preparedStatement.setString(1, namespace);
            preparedStatement.setString(2, name);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertHistoryRecord(Record record) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(History.INSERT_SQL)) {

            preparedStatement.setString(1, record.namespace);
            preparedStatement.setString(2, record.name);
            preparedStatement.setString(3, record.owner);
            preparedStatement.setString(4, record.ownerEmail);
            preparedStatement.setString(5, record.description);
            preparedStatement.setString(6, record.attributes);
            preparedStatement.setTimestamp(7, record.created);
            preparedStatement.setTimestamp(8, record.updated);
            preparedStatement.setString(9, record.trace);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Record parseRecord(ResultSet rs) throws SQLException {
        return new Record(
                rs.getString(Columns.NAMESPACE),
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
