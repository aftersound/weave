package io.aftersound.weave.zk;

import java.util.Properties;

public class ZKEnsembleConfig {

    private String name;
    private long tickTime = 2000L;
    private int initLimit = 10;
    private int syncLimit = 5;
    private String dataDir;
    private int clientPort = 2181;
    private int maxClientCnxns = 60;
    private boolean autoPurgeEnabled = false;
    private int autoPurgeSnapRetainCount = 3;
    private int autoPurgePurgeInterval = 1;
    private ZKServer[] servers;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTickTime() {
        return tickTime;
    }

    public void setTickTime(long tickTime) {
        this.tickTime = tickTime;
    }

    public int getInitLimit() {
        return initLimit;
    }

    public void setInitLimit(int initLimit) {
        this.initLimit = initLimit;
    }

    public int getSyncLimit() {
        return syncLimit;
    }

    public void setSyncLimit(int syncLimit) {
        this.syncLimit = syncLimit;
    }

    public String getDataDir() {
        return dataDir;
    }

    public void setDataDir(String dataDir) {
        this.dataDir = dataDir;
    }

    public int getClientPort() {
        return clientPort;
    }

    public void setClientPort(int clientPort) {
        this.clientPort = clientPort;
    }

    public int getMaxClientCnxns() {
        return maxClientCnxns;
    }

    public void setMaxClientCnxns(int maxClientCnxns) {
        this.maxClientCnxns = maxClientCnxns;
    }

    public boolean isAutoPurgeEnabled() {
        return autoPurgeEnabled;
    }

    public void setAutoPurgeEnabled(boolean autoPurgeEnabled) {
        this.autoPurgeEnabled = autoPurgeEnabled;
    }

    public int getAutoPurgeSnapRetainCount() {
        return autoPurgeSnapRetainCount;
    }

    public void setAutoPurgeSnapRetainCount(int autoPurgeSnapRetainCount) {
        this.autoPurgeSnapRetainCount = autoPurgeSnapRetainCount;
    }

    public int getAutoPurgePurgeInterval() {
        return autoPurgePurgeInterval;
    }

    public void setAutoPurgePurgeInterval(int autoPurgePurgeInterval) {
        this.autoPurgePurgeInterval = autoPurgePurgeInterval;
    }

    public ZKServer[] getServers() {
        return servers;
    }

    public void setServers(ZKServer[] servers) {
        this.servers = servers;
    }

    /**
     * @return
     *          properties which will be used to start embedded ZooKeeper server
     */
    public Properties properties() {
        Properties props = new Properties();

        props.setProperty("tickTime", String.valueOf(tickTime));
        props.setProperty("initLimit", String.valueOf(initLimit));
        props.setProperty("syncLimit", String.valueOf(syncLimit));
        props.setProperty("dataDir", dataDir);
        props.setProperty("clientPort", String.valueOf(clientPort));
        if (maxClientCnxns > 0) {
            props.setProperty("maxClientCnxns", String.valueOf(maxClientCnxns));
        }
        if (autoPurgeEnabled) {
            props.setProperty("autopurge.snapRetainCount", String.valueOf(autoPurgeSnapRetainCount));
            props.setProperty("autopurge.purgeInterval", String.valueOf(autoPurgePurgeInterval));
        }
        // dynamic scale in/out mode
        props.setProperty("standaloneEnabled", "false");

        // include servers
        for (ZKServer zkServer : servers) {
            props.put(zkServer.serverId(), zkServer.wildServerEntry());
        }

        return props;
    }

    /**
     * @return
     *          a connect string which client uses to establish connection to embedded ZooKeeper ensemble
     */
    public String connectString() {
        StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        for (ZKServer server : servers) {
            if (!isFirst) {
                sb.append(',');
            } else {
                isFirst = false;
            }
            sb.append(server.getHost());
        }
        sb.append(':').append(clientPort);
        return sb.toString();
    }
}
