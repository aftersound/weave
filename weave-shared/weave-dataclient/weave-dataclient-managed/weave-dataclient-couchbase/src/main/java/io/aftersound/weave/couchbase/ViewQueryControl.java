package io.aftersound.weave.couchbase;

public class ViewQueryControl {

    private long timeout;
    private long docGetTimeout;

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public long getDocGetTimeout() {
        return docGetTimeout;
    }

    public void setDocGetTimeout(long docGetTimeout) {
        this.docGetTimeout = docGetTimeout;
    }
}
