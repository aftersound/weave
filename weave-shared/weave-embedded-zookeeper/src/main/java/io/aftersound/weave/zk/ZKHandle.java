package io.aftersound.weave.zk;

import org.apache.zookeeper.ZooKeeper;

public class ZKHandle {

    private ZooKeeper zk;

    public ZooKeeper set(ZooKeeper zk) {
        ZooKeeper old = this.zk;
        this.zk = zk;
        return old;
    }

    public ZooKeeper zk() {
        return zk;
    }

}
