package io.aftersound.weave.job;

import java.util.Map;

public class JobStatus {

    private String status;
    private Map<String, Object> extra;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

}
