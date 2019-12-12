package io.aftersound.weave.zk;

public class ZKServer {

    public static class AdminServerConfig {

        private static final int DEFAULT_PORT = 8088;
        private static final int DEFAULT_IDLE_TIMEOUT = 30000;
        private static final String DEFAULT_COMMAND_URL = "/commands";
        private static final String DEFAULT_ADDRESS = "0.0.0.0";

        private boolean enabled = true;
        private String serverAddress = DEFAULT_ADDRESS;
        private int serverPort = DEFAULT_PORT;
        private int idleTimeout = DEFAULT_IDLE_TIMEOUT;
        private String commandURL = DEFAULT_COMMAND_URL;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getServerAddress() {
            return serverAddress;
        }

        public void setServerAddress(String serverAddress) {
            this.serverAddress = serverAddress;
        }

        public int getServerPort() {
            return serverPort;
        }

        public void setServerPort(int serverPort) {
            this.serverPort = serverPort;
        }

        public int getIdleTimeout() {
            return idleTimeout;
        }

        public void setIdleTimeout(int idleTimeout) {
            this.idleTimeout = idleTimeout;
        }

        public String getCommandURL() {
            return commandURL;
        }

        public void setCommandURL(String commandURL) {
            this.commandURL = commandURL;
        }

    }

    private String host;
    private String myid;
    private String dataDir;
    private int clientPort;
    private int peerPort;
    private int leaderElectionPort;

    private AdminServerConfig adminServerConfig;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
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

    public int getPeerPort() {
        return peerPort;
    }

    public void setPeerPort(int peerPort) {
        this.peerPort = peerPort;
    }

    public int getLeaderElectionPort() {
        return leaderElectionPort;
    }

    public void setLeaderElectionPort(int leaderElectionPort) {
        this.leaderElectionPort = leaderElectionPort;
    }

    public AdminServerConfig getAdminServerConfig() {
        return adminServerConfig;
    }

    public void setAdminServerConfig(AdminServerConfig adminServerConfig) {
        this.adminServerConfig = adminServerConfig;
    }

    public AdminServerConfig newAdminServerConfig() {
        return (adminServerConfig = new AdminServerConfig());
    }

    public String id() {
        return "server." + myid;
    }

    public String clientConnectString() {
        return host + ":" + clientPort;
    }

    public String wildServerEntry() {
        return new StringBuilder()
                .append("0.0.0.0")
                .append(':')
                .append(peerPort)
                .append(':')
                .append(leaderElectionPort)
                .toString();
    }

}
