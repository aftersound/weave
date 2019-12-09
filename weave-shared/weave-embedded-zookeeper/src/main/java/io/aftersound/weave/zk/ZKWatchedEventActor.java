package io.aftersound.weave.zk;

import org.apache.zookeeper.WatchedEvent;

public interface ZKWatchedEventActor {
    void act(WatchedEvent watchedEvent);
}
