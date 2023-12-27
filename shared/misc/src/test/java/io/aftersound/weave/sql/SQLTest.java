package io.aftersound.weave.sql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.*;
import org.junit.rules.TestRule;

import javax.sql.DataSource;
import java.awt.image.FilteredImageSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SQLTest {

    private static DataSource dataSource;

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
        setupDatabase();

        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl("jdbc:mysql://localhost:3306/sql_test");
        cfg.setUsername("root");
        cfg.setPassword("password");
        dataSource = new HikariDataSource(cfg);
    }

    private static void setupDatabase() throws Exception {
        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl("jdbc:mysql://localhost:3306");
        cfg.setUsername("root");
        cfg.setPassword("password");
        HikariDataSource dataSource = new HikariDataSource(cfg);
        try (Connection c = dataSource.getConnection()) {
            c.prepareStatement("CREATE DATABASE IF NOT EXISTS sql_test").execute();
        }
        dataSource.close();
    }

    @AfterClass
    public static void teardown() throws Exception {
        ((HikariDataSource) dataSource).close();
        teardownDatabase();
    }

    private static void teardownDatabase() throws Exception {
        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl("jdbc:mysql://localhost:3306");
        cfg.setUsername("root");
        cfg.setPassword("password");
        HikariDataSource dataSource = new HikariDataSource(cfg);
        try (Connection c = dataSource.getConnection()) {
            c.prepareStatement("DROP DATABASE IF EXISTS sql_test").execute();
        }
        dataSource.close();
    }

    @Test
    public void getDatabaseMetadata() {
        DatabaseMetadata dm = SQLUtils.getDatabaseMetadata(dataSource);
        assertEquals("MySQL", dm.getDatabaseProductName());
        assertNotNull(dm.getDatabaseProductVersion());
        assertEquals(4, dm.getJdbcMajorVersion());
        assertEquals(2, dm.getJdbcMinorVersion());
        assertEquals("MySQL Connector/J", dm.getDriverName());
    }

    @Test
    public void getSQLHelper() {
        SQLHelper sqlHelper = SQLUtils.getSQLHelper(dataSource);
        assertTrue(sqlHelper instanceof MySQLHelper);
        assertNotNull(sqlHelper.getDatabaseMetadata());
        assertEquals(ErrorCode.DuplicateEntry, sqlHelper.getErrorCode(new SQLException("", "", 1062)));
    }

    @Test
    public void createPredefinedStatementsFrom() throws Exception {
        SQL.PredefinedStatements predefinedStatements = SQL.PredefinedStatements.from(loadPredfinedStatementsFromJSON());
        assertNotNull(predefinedStatements.getStatement("CREATE_TABLE_PERSON"));
        assertNotNull(predefinedStatements.getStatement("ADD_PERSON"));
        assertNotNull(predefinedStatements.getStatement("GET_PERSON_BY_ID"));
        assertNotNull(predefinedStatements.getStatement("GET_ALL_PERSONS"));
        assertNotNull(predefinedStatements.getStatement("DROP_TABLE_PERSON"));
    }

    @Test
    public void testSQLStatementPrepareAndExecute() throws Exception {
        SQL.PredefinedStatements predefinedStatements = SQL.PredefinedStatements.from(loadPredfinedStatementsFromJSON());
        try (Connection c = dataSource.getConnection()) {
            int impactedRowCount = predefinedStatements.getStatement("CREATE_TABLE_PERSON")
                    .prepareAndExecute(
                            c,
                            new HashMap<>(),
                            SQL.ImpactRowCountParser.INSTANCE
                    );
            assertEquals(0, impactedRowCount);

            impactedRowCount = predefinedStatements.getStatement("ADD_PERSON")
                    .prepareAndExecute(
                            c,
                            MapBuilder.hashMap().kv("id", "ntesla").kv("first_name", "Nikola").kv("last_name", "Tesla").build(),
                            SQL.ImpactRowCountParser.INSTANCE
                    );
            assertEquals(1, impactedRowCount);

            impactedRowCount = predefinedStatements.getStatement("ADD_PERSON")
                    .prepareAndExecute(
                            c,
                            MapBuilder.hashMap().kv("id", "tedison").kv("first_name", "Thomas").kv("last_name", "Edison").build(),
                            SQL.ImpactRowCountParser.INSTANCE
                    );
            assertEquals(1, impactedRowCount);

            SQL.RowParser<Map<String, Object>> personParser =
                    rs -> MapBuilder.linkedHashMap()
                            .kv("firstName", rs.getString("first_name"))
                            .kv("lastName", rs.getString("last_name"))
                            .build();
            Map<String, Object> person = predefinedStatements.getStatement("GET_PERSON_BY_ID")
                    .prepareAndExecute(
                            c,
                            MapBuilder.hashMap().kv("id", "ntesla").build(),
                            SQL.singleRowResultParser(personParser)
                    );
            assertNotNull(person);
            assertEquals("Nikola", person.get("firstName"));
            assertEquals("Tesla", person.get("lastName"));

            assertNull(
                    predefinedStatements.getStatement("GET_PERSON_BY_ID")
                            .prepareAndExecute(
                                    c,
                                    MapBuilder.hashMap().kv("id", "noone").build(),
                                    SQL.singleRowResultParser(personParser)
                            )
            );

            List<Map<String, Object>> persons = predefinedStatements.getStatement("GET_ALL_PERSONS")
                    .prepareAndExecute(
                            c,
                            new HashMap<>(),
                            SQL.multipleRowResultParser(personParser)
                    );
            assertEquals(2, persons.size());

            impactedRowCount = predefinedStatements.getStatement("DROP_TABLE_PERSON")
                    .prepareAndExecute(
                            c,
                            new HashMap<>(),
                            SQL.ImpactRowCountParser.INSTANCE
                    );
            assertEquals(0, impactedRowCount);
        }
    }

    private static SQL.PredefinedStatement[] loadPredfinedStatementsFromJSON() throws Exception {
        try (InputStream is = SQLTest.class.getResourceAsStream("/SQLTest/predefined-statements.json")) {
            return new ObjectMapper().readValue(is, SQL.PredefinedStatement[].class);
        }
    }

}