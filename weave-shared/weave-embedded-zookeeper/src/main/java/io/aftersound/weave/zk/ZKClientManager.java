package io.aftersound.weave.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ZKClientManager implements ZKWatchedEventActor {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKClientManager.class);

    private final ZKHandle zkHandle;
    private final String connectString;
    private final int sessionTimeout;
    private final long connectTimeout;

    public ZKClientManager(ZKHandle zkHandle, String connectString, int sessionTimeout, long connectTimeout) {
        this.zkHandle = zkHandle;
        this.connectString = connectString;
        this.sessionTimeout = sessionTimeout;
        this.connectTimeout = connectTimeout;
    }

    @Override
    public void act(WatchedEvent watchedEvent) {
        try {
            connect();
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public void connect() throws Exception {
        CountDownLatch connectedSignal = new CountDownLatch(1);
        ZKWatcher zkWatcher = new ZKWatcher()
                .withEventActor(
                        Watcher.Event.KeeperState.SyncConnected,
                        new SyncConnectedEventActor(connectedSignal)
                )
                .withEventActor(
                        Watcher.Event.KeeperState.Expired,
                        this
                );
        ZooKeeper newZk = new ZooKeeper(connectString, sessionTimeout, zkWatcher);
        connectedSignal.await(connectTimeout, TimeUnit.MILLISECONDS);

        ZooKeeper oldZk = zkHandle.set(newZk);
        if (oldZk != null) {
            try {
                oldZk.close();
            } catch (Exception e) {
                LOGGER.error("failed to close expired connection", e);
            }
        }
    }

    private static class SyncConnectedEventActor implements ZKWatchedEventActor {

        private final CountDownLatch connectedSignal;

        SyncConnectedEventActor(CountDownLatch connectedSignal) {
            this.connectedSignal = connectedSignal;
        }

        @Override
        public void act(WatchedEvent watchedEvent) {
            if (watchedEvent.getState() == Watcher.Event.KeeperState.SyncConnected) {
                connectedSignal.countDown();
            }
        }

    }

}
