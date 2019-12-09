package io.aftersound.weave.zk;

import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ZKManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKManager.class);

    private final ZKHostInfo zkHostInfo;
    private final ZKEnsembleConfig zkEnsembleConfig;

    public ZKManager(ZKHostInfo zkHostInfo, ZKEnsembleConfig zkEnsembleConfig) {
        this.zkHostInfo = zkHostInfo;
        this.zkEnsembleConfig = zkEnsembleConfig;
    }

    public void init() throws Exception {
        ZKServer[] zkServers = zkEnsembleConfig.getServers();
        ZKServer targetServer = null;
        for (ZKServer zkServer : zkServers) {
            if (isTargetServer(zkHostInfo.getHostAddress(), zkHostInfo.getHostName(), zkServer)) {
                targetServer = zkServer;
                break;
            }
        }
        if (targetServer == null) {
            LOGGER.info(
                    "{}/{} is not part of embedded ZooKeeper ensemble",
                    zkHostInfo.getHostAddress(),
                    zkHostInfo.getHostName()
            );
            return;
        }

        ensureZkMyid(zkEnsembleConfig.getDataDir(), targetServer.getMyid());

        Thread zkServerThread = new Thread(() -> {
            LOGGER.info("Embedded ZooKeeper server is starting");
            System.setProperty("zookeeper.admin.serverPort", "8088");
            try {
                QuorumPeerConfig config = new QuorumPeerConfig();
                config.parseProperties(zkEnsembleConfig.properties());
                QuorumPeerMain main = new QuorumPeerMain();
                main.runFromConfig(config);
            } catch (Exception ex) {
                LOGGER.error("Embedded ZooKeeper server failed to start", ex);
                throw new RuntimeException("Embedded ZooKeeper server failed to start", ex);
            }
        });

        zkServerThread.setDaemon(true);
        zkServerThread.start();
    }

    public void shutdown() {
    }

    private void ensureZkMyid(String zkDataDir, String zkNodeMyid) throws Exception {
        Path myIdFilePath = Paths.get(zkDataDir, "myid");
        BufferedWriter writer = new BufferedWriter(new FileWriter(myIdFilePath.toFile()));
        writer.write(zkNodeMyid);
        writer.close();
    }

    private boolean isTargetServer(String hostAddress, String hostName, ZKServer zkServer) {
        if (zkServer.getHost().equals(hostAddress)) {
            return true;
        }

        if (zkServer.getHost().equals(hostName)) {
            return true;
        }

        if (zkServer.getHost().startsWith(hostName)) {
            return true;
        }

        return false;
    }

}
