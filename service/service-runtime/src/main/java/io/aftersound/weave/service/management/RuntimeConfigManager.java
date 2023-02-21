package io.aftersound.weave.service.management;

import com.fasterxml.jackson.databind.JsonNode;
import io.aftersound.weave.utils.KeyValueRepository;
import io.aftersound.weave.utils.SQLTableBasedKeyValueRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

class RuntimeConfigManager {

    private final DataSource dataSource;
    private final String table;
    private final String operator;

    public RuntimeConfigManager(DataSource dataSource, String table, String operator) {
        this.dataSource = dataSource;
        this.table = table;
        this.operator = operator;
    }

    public void createRuntimeConfig(String application, JsonNode runtimeConfigJsonNode) {
        byte[] value;
        try {
            value = Helper.MAPPER.writeValueAsBytes(runtimeConfigJsonNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = dataSource.getConnection()) {
            KeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, table, operator);
            kvp.insert(application, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> getRuntimeConfig(String application) {
        byte[] value;
        try (Connection connection = dataSource.getConnection()) {
            KeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, table, operator);
            value = kvp.get(application);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (value == null) {
            return null;
        }

        try {
            return Helper.MAPPER.readValue(value, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRuntimeConfig(String application, JsonNode runtimeConfigJsonNode) {
        byte[] value;
        try {
            value = Helper.MAPPER.writeValueAsBytes(runtimeConfigJsonNode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = dataSource.getConnection()) {
            KeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, table, operator);
            kvp.update(application, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteRuntimeConfig(String application) {
        try (Connection connection = dataSource.getConnection()) {
            KeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, table, operator);
            kvp.delete(application);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
