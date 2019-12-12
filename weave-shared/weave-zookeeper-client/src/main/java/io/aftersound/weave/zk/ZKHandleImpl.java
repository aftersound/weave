package io.aftersound.weave.zk;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

class ZKHandleImpl extends ZKHandleBase {

    private final ZKWatcher zkWatcher;
    private ZKConnection zkConnection;

    ZKHandleImpl() {
        this.zkWatcher = new ZKWatcher();
    }

    ZKConnection newConnection(String connectString, int sessionTimeout, long connectTimeout) {
        if (zkConnection == null) {
            zkConnection = new ZKConnection(connectString, sessionTimeout, connectTimeout, this, zkWatcher);
        }
        return zkConnection;
    }

    @Override
    public void close() throws IOException {
        zkWatcher.detachEventActors();
        ZooKeeper zk = get();
        if (zk != null) {
            try {
                zk.close();
            } catch (InterruptedException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void bindEventActor(ZKWatchedEventActor eventActor) {
        // Placeholder for now
    }
}
