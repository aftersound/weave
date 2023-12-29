package io.aftersound.weave.service.management;

import com.fasterxml.jackson.databind.JsonNode;
import io.aftersound.weave.utils.KeyValueRepository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

class SpecManager {
/*
    private final DataSource dataSource;
    private final String table;
    private final String operator;

    public SpecManager(DataSource dataSource, String table, String operator) {
        this.dataSource = dataSource;
        this.table = table;
        this.operator = operator;
    }

    public void create(String id, JsonNode spec) {
        byte[] value;
        try {
            value = Helper.MAPPER.writeValueAsBytes(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = dataSource.getConnection()) {
            KeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, table, operator);
            kvp.insert(id, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, Object> get(String id) {
        byte[] value;
        try (Connection connection = dataSource.getConnection()) {
            KeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, table, operator);
            value = kvp.get(id);
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

    public void update(String id, JsonNode spec) {
        byte[] value;
        try {
            value = Helper.MAPPER.writeValueAsBytes(spec);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        try (Connection connection = dataSource.getConnection()) {
            KeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, table, operator);
            kvp.update(id, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String id) {
        try (Connection connection = dataSource.getConnection()) {
            KeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, table, operator);
            kvp.delete(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
*/
}
