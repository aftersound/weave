package io.aftersound.weave.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;

public class DefaultExtensionRegistry implements ExtensionRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultExtensionRegistry.class);

    private static final String DEFAULT_TABLE = "EXTENSION_INFO";
    private static final String INSERT = "INSERT INTO TABLE(_group,name,version,base_type,type,jar_location) VALUES(?,?,?,?,?,?)";
    private static final String UPDATE = "UPDATE TABLE SET base_type=?, type=?, jar_location=? WHERE _group=? AND name=? AND version=?";
    private static final String SELECT = "SELECT * FROM TABLE WHERE _group=? AND name=? AND version=?";

    private final DataSource dataSource;
    private final String insertSql;
    private final String updateSql;
    private final String selectSql;

    public DefaultExtensionRegistry(DataSource dataSource, String table) {
        assert dataSource != null : "DataSource is null";
        this.dataSource = dataSource;

        LOGGER.info("Extension info registration is managed in SQL table '{}'", table);
        this.insertSql = INSERT.replace("TABLE", table);
        this.updateSql = UPDATE.replace("TABLE", table);
        this.selectSql = SELECT.replace("TABLE", table);
    }

    public DefaultExtensionRegistry(DataSource dataSource) {
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
    public ExtensionInfo get(String group, String name, String version) {
        try (Connection c = dataSource.getConnection()) {
            PreparedStatement preparedStatement = c .prepareStatement(selectSql);
            preparedStatement.setString(1, group);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, version);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return ExtensionInfoBuilder.extensionInfo(
                        group,
                        name,
                        version,
                        resultSet.getString("base_type"),
                        resultSet.getString("type"),
                        resultSet.getString("jar_location")
                );
            } else {
                return null;
            }
        } catch (SQLException e) {
            String msg = String.format("Failed to get extension with (group=%s,name=%s,version=%s)", group, name, version);
            throw new RuntimeException(msg, e);
        }
    }

}
