package io.aftersound.weave.zk;

import org.apache.commons.io.FileUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;

public class ZKManagerTest {

    private static String dataDir1;
    private static String dataDir2;
    private static String dataDir3;

    private ZKManager zkManager;

    @BeforeClass
    public static void beforeClass() throws Exception {
        ZKManagerTest.dataDir1 = createZkDataDir("1");
        ZKManagerTest.dataDir2 = createZkDataDir("2");
        ZKManagerTest.dataDir3 = createZkDataDir("3");
    }

    private static String createZkDataDir(String id) throws Exception {
        Path dataDir = Paths.get(System.getProperty("java.io.tmpdir"), ZKManagerTest.class.getSimpleName(), "zkdata" + id);
        Files.createDirectories(dataDir);
        return dataDir.toString();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        FileUtils.deleteDirectory(new File(dataDir1));
        FileUtils.deleteDirectory(new File(dataDir2));
        FileUtils.deleteDirectory(new File(dataDir3));
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
        ensembleConfig.setAutoPurgeEnabled(true);

        ZKServer s1 = new ZKServer();
        s1.setHost("localhost");
        s1.setMyid("1");
        s1.setDataDir(dataDir1);
        s1.setClientPort(2181);
        s1.setPeerPort(2182);
        s1.setLeaderElectionPort(2183);

        ZKServer s2 = new ZKServer();
        s2.setHost("localhost");
        s2.setMyid("2");
        s2.setDataDir(dataDir2);
        s2.setClientPort(2184);
        s2.setPeerPort(2185);
        s2.setLeaderElectionPort(2186);

        ZKServer s3 = new ZKServer();
        s3.setHost("localhost");
        s3.setMyid("3");
        s3.setDataDir(dataDir3);
        s3.setClientPort(2187);
        s3.setPeerPort(2188);
        s3.setLeaderElectionPort(2189);

        ensembleConfig.setAdminServerPort(8088);
        ensembleConfig.setServers(Arrays.asList(s1, s2, s3).toArray(new ZKServer[3]));

        zkManager = new ZKManager(hostInfo, ensembleConfig);
        zkManager.init();

        ZKHandle zkHandle = new ZKHandle();
        ZKClientManager zkClientManager = new ZKClientManager(zkHandle, ensembleConfig.connectString(), 100000, 100000L);
        zkClientManager.connect();
        assertNotNull(zkHandle.zk());

        zkHandle.zk().create("/test", new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.CONTAINER);

        Stat stat = zkHandle.zk().exists("/test", false);
        assertNotNull(stat);

        zkHandle.zk().close();

        zkManager.shutdown();
    }

}