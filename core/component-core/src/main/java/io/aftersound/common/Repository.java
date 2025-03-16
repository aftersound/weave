package io.aftersound.common;

import java.util.Map;

/**
 * Capture controls with regard to access data in repository
 */
public class Repository {

    /**
     * Client identifier which is able to access data repository
     */
    private String clientId;

    /**
     * Options that control how to access repository, such as
     *   read timeout
     *   read failover
     * etc.
     */
    private Map<String, String> accessControl;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Map<String, String> getAccessControl() {
        return accessControl;
    }

    public void setAccessControl(Map<String, String> accessControl) {
        this.accessControl = accessControl;
    }
}
