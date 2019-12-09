package io.aftersound.weave.zk;

public class ZKServer {

    private String host;
    private String myid;
    private String ports;

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

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public String serverId() {
        return "server." + myid;
    }

    public String serverEntry() {
        return host + ":" + ports;
    }

    public String wildServerEntry() {
        return "0.0.0.0:" + ports;
    }
}
