package io.aftersound.library;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.*;
import org.junit.rules.TestRule;
import org.junit.runners.model.Statement;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * This test needs an MySql server at local.
 * To set up a MySql server using docker technology,
 *  docker run --name weave-mysql -e MYSQL_ROOT_PASSWORD=password -d -p 3306:3306 mysql:5.7.38
 */
public class SQLTableBasedExtensionRegistryTest {

    @ClassRule
    public static TestRule testRule = (base, description) -> new Statement() {

        @Override
        public void evaluate() throws Throwable {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress("localhost", 3306), 3000);
                base.evaluate();
            } catch (IOException e) {
                throw new AssumptionViolatedException("Could not connect to local MySQL server. Skipping test!");
            }
        }
    };

    private static final String TABLE = "EXTENSION_INFO_TEST";
    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS EXTENSION_INFO_TEST ( _group VARCHAR(255), name VARCHAR(255), version VARCHAR(255), base_type VARCHAR(1024) NOT NULL, type VARCHAR(1024) NOT NULL, jar_location VARCHAR(1024) NOT NULL, PRIMARY KEY (_group, name, version));";
    private static final String DROP_TABLE_SQL = "DROP TABLE EXTENSION_INFO_TEST;";

    private static DataSource dataSource;
    private static ExtensionRegistry extensionRegistry;

    @BeforeClass
    public static void setup() throws Exception {
        HikariConfig cfg = new HikariConfig();
        cfg.setDriverClassName("com.mysql.cj.jdbc.Driver");
        cfg.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        cfg.setUsername("root");
        cfg.setPassword("password");
        dataSource = new HikariDataSource(cfg);

        try (Connection c = dataSource.getConnection()) {
            PreparedStatement preparedStatement = c.prepareStatement(CREATE_TABLE_SQL);
            preparedStatement.execute();
        }

        extensionRegistry = new SQLTableBasedExtensionRegistry(dataSource, TABLE);
    }

    @AfterClass
    public static void teardown() throws Exception {
        try (Connection c = dataSource.getConnection()) {
            PreparedStatement preparedStatement = c.prepareStatement(DROP_TABLE_SQL);
            preparedStatement.execute();
        }
    }

    @Test
    public void register() {
        final String[][] extensionInfos = {
                { "BEAM_PIPELINE_COMPOSER", "Count", "0.0.1-SNAPSHOT", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.CountComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-common-extensions/0.0.1-SNAPSHOT/beam-common-extensions-0.0.1-SNAPSHOT.jar" },
                { "BEAM_PIPELINE_COMPOSER", "Create", "0.0.1-SNAPSHOT", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.CreateComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-common-extensions/0.0.1-SNAPSHOT/beam-common-extensions-0.0.1-SNAPSHOT.jar" },
                { "BEAM_PIPELINE_COMPOSER", "FlatMap", "0.0.1-SNAPSHOT", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.FlatMapComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-common-extensions/0.0.1-SNAPSHOT/beam-common-extensions-0.0.1-SNAPSHOT.jar" },
                { "BEAM_PIPELINE_COMPOSER", "Function", "0.0.1-SNAPSHOT", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.FunctionComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-common-extensions/0.0.1-SNAPSHOT/beam-common-extensions-0.0.1-SNAPSHOT.jar" },
                { "BEAM_PIPELINE_COMPOSER", "Map", "0.0.1-SNAPSHOT", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.MapComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-common-extensions/0.0.1-SNAPSHOT/beam-common-extensions-0.0.1-SNAPSHOT.jar" },
                { "BEAM_PIPELINE_COMPOSER", "TextIO.read", "0.0.1-SNAPSHOT", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.TextIOReadComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-common-extensions/0.0.1-SNAPSHOT/beam-common-extensions-0.0.1-SNAPSHOT.jar" },
                { "BEAM_PIPELINE_COMPOSER", "TextIO.write", "0.0.1-SNAPSHOT", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.TextIOWriteComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-common-extensions/0.0.1-SNAPSHOT/beam-common-extensions-0.0.1-SNAPSHOT.jar" },
                { "BEAM_PIPELINE_COMPOSER", "MinimalWordCount", "0.0.1-SNAPSHOT", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.MinimalWordCountPipelineComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-customized-extensions-test/0.0.1-SNAPSHOT/beam-customized-extensions-test-0.0.1-SNAPSHOT.jar" },
                { "BEAM_PIPELINE_COMPOSER", "MinimalWordCount", "0.0.1-SNAPSHOT", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.MinimalWordCountPipelineComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-customized-extensions-test/0.0.1-SNAPSHOT/beam-customized-extensions-test-0.0.1-SNAPSHOT.jar" }
        };

        for (String[] ei : extensionInfos) {
            ExtensionInfo extensionInfo = ExtensionInfoBuilder.extensionInfo(ei[0],ei[1],ei[2],ei[3],ei[5],ei[5]);
            extensionRegistry.register(extensionInfo);
        }
    }

    @Test(expected = RuntimeException.class)
    public void registerDuplicate() {
        final String[][] extensionInfos = {
                { "BEAM_PIPELINE_COMPOSER", "MinimalWordCount", "0.0.1", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.MinimalWordCountPipelineComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-customized-extensions-test/0.0.1/beam-customized-extensions-test-0.0.1.jar" },
                { "BEAM_PIPELINE_COMPOSER", "MinimalWordCount", "0.0.1", "io.aftersound.weave.beam.PipelineComposer", "io.aftersound.weave.beam.MinimalWordCountPipelineComposer", "file:///home/xuxiaochun/.m2/repository/io/aftersound/weave/beam-customized-extensions-test/0.0.1/beam-customized-extensions-test-0.0.1.jar" }
        };

        for (String[] ei : extensionInfos) {
            ExtensionInfo extensionInfo = ExtensionInfoBuilder.extensionInfo(ei[0],ei[1],ei[2],ei[3],ei[5],ei[5]);
            extensionRegistry.register(extensionInfo);
        }
    }

    @Test
    public void list() {
        List<ExtensionInfo> extensionInfoList = extensionRegistry.list();
        assertEquals(9, extensionInfoList.size());
    }

    @Test
    public void get() {
        ExtensionInfo extensionInfo;

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "Count", "0.0.1-SNAPSHOT");
        assertNotNull(extensionInfo);
        Map<String, String> m = extensionInfo.asMap();
        assertNotNull(m);
        assertEquals("Count", m.get("name"));

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "Create", "0.0.1-SNAPSHOT");
        assertNotNull(extensionInfo);

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "FlatMap", "0.0.1-SNAPSHOT");
        assertNotNull(extensionInfo);

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "Function", "0.0.1-SNAPSHOT");
        assertNotNull(extensionInfo);

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "Map", "0.0.1-SNAPSHOT");
        assertNotNull(extensionInfo);

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "TextIO.read", "0.0.1-SNAPSHOT");
        assertNotNull(extensionInfo);

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "TextIO.write", "0.0.1-SNAPSHOT");
        assertNotNull(extensionInfo);

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "MinimalWordCount", "0.0.1-SNAPSHOT");
        assertNotNull(extensionInfo);

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "NotExist", "0.0.1-SNAPSHOT");
        assertNull(extensionInfo);

        extensionInfo = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "Count", "0.0.100-SNAPSHOT");
        assertNull(extensionInfo);

        List<ExtensionInfo> extensionInfoList = extensionRegistry.get("BEAM_PIPELINE_COMPOSER", "Count");
        assertEquals(1, extensionInfoList.size());

        extensionInfoList = extensionRegistry.get("BEAM_PIPELINE_COMPOSER");
        assertEquals(9, extensionInfoList.size());
    }
}