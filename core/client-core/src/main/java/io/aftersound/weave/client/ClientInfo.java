package io.aftersound.weave.client;

public class ClientInfo {

    private String id;
    private String controlType;
    private String clientType;
    private Endpoint endpoint;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

}
