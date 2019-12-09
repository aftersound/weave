package io.aftersound.weave.zk;

public class ZKServer {

    private String host;
    private String myid;
    private String dataDir;
    private int clientPort;
    private int peerPort;
    private int leaderElectionPort;

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
