package io.aftersound.weave.service.management;

import com.fasterxml.jackson.core.type.TypeReference;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

class NamespaceManager {

    private interface Columns {
        String ID = "id";
        String OWNER = "owner";
        String OWNER_EMAIL = "owner_email";
        String ATTRIBUTES = "attributes";
        String DESCRIPTION = "description";
        String CREATED = "created";
        String UPDATED = "updated";
    }

    private static final String CREATE_NAMESPACE_SQL = "INSERT INTO namespace (id,owner,owner_email,description,attributes,created,updated) VALUES(?,?,?,?,?,?,?)";
    private static final String GET_NAMESPACE_SQL = "SELECT * FROM namespace WHERE id=?";
    private static final String UPDATE_NAMESPACE_SQL = "UPDATE namespace SET owner=?,owner_email=?,description=?,attributes=?,updated=? WHERE id=?";
    private static final String DELETE_NAMESPACE_SQL = "DELETE FROM namespace WHERE id=?";
    private static final String FIND_NAMESPACES_BY_OWNER_SQL = "SELECT * FROM namespace WHERE owner IN (%s)";

    private final DataSource dataSource;

    public NamespaceManager(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void createNamespace(Namespace namespace) {
        String attributes = null;
        if (namespace.getAttributes() != null) {
            try {
                attributes = Helper.MAPPER.writeValueAsString(namespace.getAttributes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Timestamp created, updated;
        created = updated = new Timestamp(System.currentTimeMillis());

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CREATE_NAMESPACE_SQL)) {
            preparedStatement.setString(1, namespace.getId());
            preparedStatement.setString(2, namespace.getOwner());
            preparedStatement.setString(3, namespace.getOwnerEmail());
            preparedStatement.setString(4, namespace.getDescription());
            preparedStatement.setString(5, attributes);
            preparedStatement.setTimestamp(6, created);
            preparedStatement.setTimestamp(7, updated);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Namespace getNamespace(String id) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(GET_NAMESPACE_SQL)) {
            preparedStatement.setString(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return parseNamespace(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateNamespace(Namespace namespace) {
        String attributes = null;
        if (namespace.getAttributes() != null) {
            try {
                attributes = Helper.MAPPER.writeValueAsString(namespace.getAttributes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        Timestamp updated = new Timestamp(System.currentTimeMillis());

        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_NAMESPACE_SQL)) {
            preparedStatement.setString(1, namespace.getOwner());
            preparedStatement.setString(2, namespace.getOwnerEmail());
            preparedStatement.setString(3, namespace.getDescription());
            preparedStatement.setString(4, attributes);
            preparedStatement.setTimestamp(5, updated);
            preparedStatement.setString(6, namespace.getId());

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteNamespace(String namespace) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_NAMESPACE_SQL)) {
            preparedStatement.setString(1, namespace);

            preparedStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
                namespaces.add(parseNamespace(rs));
            }
            return namespaces;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static Namespace parseNamespace(ResultSet rs) throws SQLException {
        String id = rs.getString(Columns.ID);
        String owner = rs.getString(Columns.OWNER);
        String ownerEmail = rs.getString(Columns.OWNER_EMAIL);
        String description = rs.getString(Columns.DESCRIPTION);
        String attributes = rs.getString(Columns.ATTRIBUTES);

        Namespace ns = new Namespace();
        ns.setId(id);
        ns.setOwner(owner);
        ns.setOwnerEmail(ownerEmail);
        ns.setDescription(description);
        ns.setAttributes(parseAttributes(attributes));
        return ns;
    }

    private static Map<String, String> parseAttributes(String json) {
        try {
            return Helper.MAPPER.readValue(json, new TypeReference<Map<String, String>>() {});
        } catch (Exception e) {
            return null;
        }
    }

}
