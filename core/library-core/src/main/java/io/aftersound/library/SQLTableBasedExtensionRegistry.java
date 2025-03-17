package io.aftersound.library;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class SQLTableBasedExtensionRegistry implements ExtensionRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQLTableBasedExtensionRegistry.class);

    private static final String DEFAULT_TABLE = "EXTENSION_INFO";
    private static final String INSERT = "INSERT INTO TABLE(_group,name,version,base_type,type,jar_location) VALUES(?,?,?,?,?,?)";
    private static final String UPDATE = "UPDATE TABLE SET base_type=?, type=?, jar_location=? WHERE _group=? AND name=? AND version=?";
    private static final String SELECT_ALL = "SELECT * FROM TABLE";
    private static final String SELECT_BY_GROUP_NAME_VERSION = "SELECT * FROM TABLE WHERE _group=? AND name=? AND version=?";
    private static final String SELECT_BY_GROUP_NAME = "SELECT * FROM TABLE WHERE _group=? AND name=?";
    private static final String SELECT_BY_GROUP = "SELECT * FROM TABLE WHERE _group=?";

    private final DataSource dataSource;
    private final String insertSql;
    private final String updateSql;
    private final String sqlForList;
    private final String sqlForGet3;    // by group, name, version
    private final String sqlForGet2;    // by group, name
    private final String sqlForGet1;    // by group

    public SQLTableBasedExtensionRegistry(DataSource dataSource, String table) {
        assert dataSource != null : "DataSource is null";
        this.dataSource = dataSource;

        LOGGER.info("Extension info registration is managed in SQL table '{}'", table);
        this.insertSql = INSERT.replace("TABLE", table);
        this.updateSql = UPDATE.replace("TABLE", table);
        this.sqlForList = SELECT_ALL.replace("TABLE", table);
        this.sqlForGet3 = SELECT_BY_GROUP_NAME_VERSION.replace("TABLE", table);
        this.sqlForGet2 = SELECT_BY_GROUP_NAME.replace("TABLE", table);
        this.sqlForGet1 = SELECT_BY_GROUP.replace("TABLE", table);
    }

    public SQLTableBasedExtensionRegistry(DataSource dataSource) {
        this(dataSource, DEFAULT_TABLE);
    }

    @Override
    public void register(ExtensionInfo extensionInfo) {
        if (extensionInfo != null) {
            try (Connection c = dataSource.getConnection()) {
                PreparedStatement insertStatement = c .prepareStatement(insertSql);
                insertStatement.setString(1, extensionInfo.getGroup());
                insertStatement.setString(2, extensionInfo.getName());
                insertStatement.setString(3, extensionInfo.getVersion());
                insertStatement.setString(4, extensionInfo.getBaseType());
                insertStatement.setString(5, extensionInfo.getType());
                insertStatement.setString(6, extensionInfo.getJarLocation());

                try {
                    insertStatement.execute();
                    LOGGER.info("Extension {} is successfully registered", extensionInfo);
                } catch (SQLIntegrityConstraintViolationException sicve) {
                    final boolean isSnapshotVersion = extensionInfo.getVersion().contains("SNAPSHOT");
                    final String message = sicve.getMessage();
                    if (isSnapshotVersion && message.contains("Duplicate entry")) {
                        LOGGER.info("Extension {} is successfully registered with previous snapshot being overridden", extensionInfo);
                        PreparedStatement updateStatement = c .prepareStatement(updateSql);
                        updateStatement.setString(1, extensionInfo.getBaseType());
                        updateStatement.setString(2, extensionInfo.getType());
                        updateStatement.setString(3, extensionInfo.getJarLocation());
                        updateStatement.setString(4, extensionInfo.getGroup());
                        updateStatement.setString(5, extensionInfo.getName());
                        updateStatement.setString(6, extensionInfo.getVersion());
                        updateStatement.execute();
                    } else {
                        throw sicve;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to register extension: " + extensionInfo, e);
            }
        }
    }

    @Override
    public List<ExtensionInfo> list() {
        try (Connection c = dataSource.getConnection()) {
            PreparedStatement preparedStatement = c.prepareStatement(sqlForList);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExtensionInfo> extensionInfoList = new LinkedList<>();
            while (resultSet.next()) {
                ExtensionInfo ei = parseExtensionInfo(resultSet);
                extensionInfoList.add(ei);
            }
            return extensionInfoList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ExtensionInfo get(String group, String name, String version) {
        try (Connection c = dataSource.getConnection()) {
            PreparedStatement preparedStatement = c.prepareStatement(sqlForGet3);
            preparedStatement.setString(1, group);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, version);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return parseExtensionInfo(resultSet);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExtensionInfo> get(String group, String name) {
        try (Connection c = dataSource.getConnection()) {
            PreparedStatement preparedStatement = c.prepareStatement(sqlForGet2);
            preparedStatement.setString(1, group);
            preparedStatement.setString(2, name);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExtensionInfo> extensionInfoList = new LinkedList<>();
            while (resultSet.next()) {
                ExtensionInfo ei = parseExtensionInfo(resultSet);
                extensionInfoList.add(ei);
            }
            return extensionInfoList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ExtensionInfo> get(String group) {
        try (Connection c = dataSource.getConnection()) {
            PreparedStatement preparedStatement = c.prepareStatement(sqlForGet1);
            preparedStatement.setString(1, group);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExtensionInfo> extensionInfoList = new LinkedList<>();
            while (resultSet.next()) {
                ExtensionInfo ei = parseExtensionInfo(resultSet);
                extensionInfoList.add(ei);
            }
            return extensionInfoList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static ExtensionInfo parseExtensionInfo(ResultSet resultSet) throws SQLException {
        return ExtensionInfoBuilder.extensionInfo(
                resultSet.getString("_group"),
                resultSet.getString("name"),
                resultSet.getString("version"),
                resultSet.getString("base_type"),
                resultSet.getString("type"),
                resultSet.getString("jar_location")
        );
    }

}
