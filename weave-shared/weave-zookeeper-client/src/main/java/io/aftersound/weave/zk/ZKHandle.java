package io.aftersound.weave.zk;

import org.apache.zookeeper.ZooKeeper;

import java.io.Closeable;

public interface ZKHandle extends Closeable {
    ZooKeeper get();
    void bindEventActor(ZKWatchedEventActor eventActor);
}
