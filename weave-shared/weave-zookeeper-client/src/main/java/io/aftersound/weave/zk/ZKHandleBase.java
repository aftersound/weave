package io.aftersound.weave.zk;

import org.apache.zookeeper.ZooKeeper;

public abstract class ZKHandleBase implements ZKHandle {

    private ZooKeeper zk;

    ZooKeeper set(ZooKeeper zk) {
        ZooKeeper old = this.zk;
        this.zk = zk;
        return old;
    }

    public ZooKeeper get() {
        return zk;
    }
}
