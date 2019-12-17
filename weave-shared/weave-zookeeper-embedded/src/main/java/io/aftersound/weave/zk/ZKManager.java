package io.aftersound.weave.zk;

import net.bytebuddy.agent.ByteBuddyAgent;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig;
import org.apache.zookeeper.server.quorum.QuorumPeerMain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ZKManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKManager.class);

    private final ZKHostInfo zkHostInfo;
    private final ZKEnsembleConfig zkEnsembleConfig;

    public ZKManager(ZKHostInfo zkHostInfo, ZKEnsembleConfig zkEnsembleConfig) {
        this.zkHostInfo = zkHostInfo;
        this.zkEnsembleConfig = zkEnsembleConfig;
    }

    public void init() throws Exception {
        if (zkEnsembleConfig == null) {
            return;
        }

        if (zkEnsembleConfig.isZkCustomizationEnabled()) {
            ByteBuddyAgent.install();
            ZKCustomization.init();
        }

        ZKServer[] zkServers = zkEnsembleConfig.getServers();
        List<ZKServer> targetServers = new ArrayList<>();
        for (ZKServer zkServer : zkServers) {
            if (isTargetServer(zkHostInfo.getHostAddress(), zkHostInfo.getHostName(), zkServer)) {
                targetServers.add(zkServer);
            }
        }
        if (targetServers.isEmpty()) {
            LOGGER.info(
                    "{}/{} is not part of embedded ZooKeeper ensemble",
                    zkHostInfo.getHostAddress(),
                    zkHostInfo.getHostName()
            );
            return;
        }

        for (ZKServer zkServer : targetServers) {
            Thread zkServerThread = new Thread(() -> {
                try {
                    // passing ZKAdminServerConfig via ThreadLocal
                    ZKAdminServerConfigHolder.set(zkServer.getAdminServerConfig());

                    String myid = zkServer.getMyid();
                    ensureZkMyid(zkServer.getDataDir(), myid);
                    LOGGER.info("Embedded ZooKeeper server is starting");

                    QuorumPeerConfig config = new QuorumPeerConfig();
                    config.parseProperties(zkEnsembleConfig.properties(myid));
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
