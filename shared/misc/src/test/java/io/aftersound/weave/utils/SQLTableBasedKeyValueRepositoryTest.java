package io.aftersound.weave.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.*;
import org.junit.rules.TestRule;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.*;

public class SQLTableBasedKeyValueRepositoryTest {

    private static HikariDataSource dataSource;

    @ClassRule
    public static TestRule testRule = (base, description) -> new org.junit.runners.model.Statement() {

        @Override
        public void evaluate() throws Throwable {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", 3306), 3000);
            } catch (IOException e) {
                throw new AssumptionViolatedException("Could not connect to local MySQL server. Skipping test!");
            }

            base.evaluate();
        }
    };

    @BeforeClass
    public static void setup() throws Exception {
        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        cfg.setUsername("root");
        cfg.setPassword("password");
        dataSource = new HikariDataSource(cfg);

        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE kv")) {
                ps.execute();
            }
            try (PreparedStatement ps = connection.prepareStatement("TRUNCATE TABLE kv_history")) {
                ps.execute();
            }
        }
    }

    @AfterClass
    public static void teardown() throws Exception {
        dataSource.close();
    }

    @Test
    public void testSingleOperation() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            SQLTableBasedKeyValueRepository kvp = SQLTableBasedKeyValueRepository.create(connection, "kv");
            kvp.insert("p1", "Nikola Teslaa".getBytes());
            assertEquals("Nikola Teslaa", new String(kvp.get("p1")));
            kvp.update("p1", "Nikola Tesla".getBytes());
            assertEquals("Nikola Tesla", new String(kvp.get("p1")));
            kvp.delete("p1");
            assertNull(kvp.get("p1"));
        }
    }

    @Test
    public void testSingleOperationWithHistory() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            SQLTableBasedKeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, "kv", "system");
            kvp.insert("p2", "Nikola Teslaa".getBytes());
            Thread.sleep(100L);
            assertEquals("Nikola Teslaa", new String(kvp.get("p2")));

            kvp.update("p2", "Nikola Tesla".getBytes());
            Thread.sleep(100L);
            assertEquals("Nikola Tesla", new String(kvp.get("p2")));

            kvp.delete("p2");
            Thread.sleep(100L);
            assertNull(kvp.get("p2"));

            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM kv_history WHERE k='p2'")) {
                ResultSet rs = preparedStatement.executeQuery();
                int count = 0;
                while (rs.next()) {
                    count++;
                }
                assertEquals(3, count);
            }
        }
    }

    @Test
    public void testBulkOperation() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            SQLTableBasedKeyValueRepository kvp = SQLTableBasedKeyValueRepository.create(connection, "kv");

            kvp.bulkInsert(
                    MapBuilder.linkedHashMap()
                            .kv("p3", "Thomas Edisonn".getBytes())
                            .kv("p4", "Benjamin Franklinn".getBytes())
                            .build()
            );

            Map<String, byte[]> m = kvp.bulkGet(Arrays.asList("p3", "p4"));
            assertEquals("Thomas Edisonn", new String(m.get("p3")));
            assertEquals("Benjamin Franklinn", new String(m.get("p4")));

            kvp.bulkUpdate(
                    MapBuilder.linkedHashMap()
                            .kv("p3", "Thomas Edison".getBytes())
                            .kv("p4", "Benjamin Franklin".getBytes())
                            .build()
            );
            m = kvp.bulkGet(Arrays.asList("p3", "p4"));
            assertEquals("Thomas Edison", new String(m.get("p3")));
            assertEquals("Benjamin Franklin", new String(m.get("p4")));

            kvp.bulkDelete(Arrays.asList("p3", "p4"));
            m = kvp.bulkGet(Arrays.asList("p3", "p4"));
            assertTrue(m.isEmpty());
        }
    }

    @Test
    public void testBulkOperationWithHistory() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            SQLTableBasedKeyValueRepository kvp = SQLTableBasedKeyValueRepository.createWithHistory(connection, "kv", "test");

            kvp.bulkInsert(
                    MapBuilder.linkedHashMap()
                            .kv("p5", "Thomas Edisonn".getBytes())
                            .kv("p6", "Benjamin Franklinn".getBytes())
                            .build()
            );
            Thread.sleep(100L);

            Map<String, byte[]> m = kvp.bulkGet(Arrays.asList("p5", "p6"));
            assertEquals("Thomas Edisonn", new String(m.get("p5")));
            assertEquals("Benjamin Franklinn", new String(m.get("p6")));

            kvp.bulkUpdate(
                    MapBuilder.linkedHashMap()
                            .kv("p5", "Thomas Edison".getBytes())
                            .kv("p6", "Benjamin Franklin".getBytes())
                            .build()
            );
            Thread.sleep(100L);
            m = kvp.bulkGet(Arrays.asList("p5", "p6"));
            assertEquals("Thomas Edison", new String(m.get("p5")));
            assertEquals("Benjamin Franklin", new String(m.get("p6")));

            kvp.bulkDelete(Arrays.asList("p5", "p6"));
            Thread.sleep(100L);
            m = kvp.bulkGet(Arrays.asList("p5", "p6"));
            assertTrue(m.isEmpty());

            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM kv_history WHERE k in ('p5', 'p6')")) {
                ResultSet rs = preparedStatement.executeQuery();
                int count = 0;
                while (rs.next()) {
                    count++;
                }
                assertEquals(6, count);
            }
        }
    }
}