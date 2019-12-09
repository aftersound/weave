package io.aftersound.weave.zk;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class ZKManagerTest {

    private static String dataDir;

    private ZKManager zkManager;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Path dataDir = Paths.get(System.getProperty("java.io.tmpdir"), ZKManagerTest.class.getSimpleName());
        Files.createDirectories(dataDir);
        ZKManagerTest.dataDir = dataDir.toString();
    }

    @AfterClass
    public static void afterClass() {
    }

    @Test
    public void initShutdown() throws Exception {

        ZKHostInfo hostInfo = new ZKHostInfo() {

            @Override
            public String getHostName() {
                return "localhost";
            }

            @Override
            public String getHostAddress() {
                return "127.0.0.1";
            }

        };
        ZKEnsembleConfig ensembleConfig = new ZKEnsembleConfig();
        ensembleConfig.setName("test");
        ensembleConfig.setDataDir(dataDir);
        ensembleConfig.setAutoPurgeEnabled(true);

        ZKServer server = new ZKServer();
        server.setHost("localhost");
        server.setMyid("1");
        server.setPorts("2888:3888");
        ensembleConfig.setServers(Arrays.asList(server).toArray(new ZKServer[1]));

        zkManager = new ZKManager(hostInfo, ensembleConfig);
        zkManager.init();

        zkManager.shutdown();
    }

}