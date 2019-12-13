package io.aftersound.weave.zk;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class ZKLock {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZKLock.class);

    private final ZooKeeper zk;
    private final String lockBasePath;
    private final String lockName;

    private String lockPath;

    public ZKLock(ZooKeeper zk, String[] lockBasePath, String lockName) {
        this.zk = zk;
        this.lockBasePath = assemble(lockBasePath);
        this.lockName = lockName;
    }

    private static String assemble(String[] lockBasePath) {
        if (lockBasePath == null || lockBasePath.length == 0) {
            throw new IllegalArgumentException("lockBasePath cannot be null or empty");
        }

        StringBuilder sb = new StringBuilder();
        for (String section : lockBasePath) {
            if (section == null || section.isEmpty() || section.indexOf('/') >= 0) {
                throw new IllegalArgumentException("lockBasePath contains unacceptable section as "+ section);
            }
            sb.append('/');
            sb.append(section);
        }
        return sb.toString();
    }


    public void lock() throws IOException {
        try {
            // lockPath will be something link lockBasePath + "/" + lockName + "-SEQ"
            // because ZooKeeper appends sequence number
            lockPath = zk.create(
                    lockBasePath + "/" + lockName,
                    null,
                    ZooDefs.Ids.OPEN_ACL_UNSAFE,
                    CreateMode.EPHEMERAL_SEQUENTIAL
            );
            final Object lock = new Object();
            synchronized(lock) {
                while (true) {
                    List<String> nodes = zk.getChildren(lockBasePath, new Watcher() {
                        @Override
                        public void process(WatchedEvent event) {
                            synchronized (lock) {
                                lock.notifyAll();
                            }
                        }
                    });
                    Collections.sort(nodes); // ZooKeeper node names can be sorted lexicographically
                    if (lockPath.endsWith(nodes.get(0))) {
                        return;
                    } else {
                        lock.wait();    // until timeout
                    }
                }
            }
        } catch (KeeperException e) {
            throw new IOException (e);
        } catch (InterruptedException e) {
            throw new IOException (e);
        }
    }

    public void unlock() throws IOException {
        try {
            zk.delete(lockPath, -1);
            lockPath = null;
        } catch (KeeperException e) {
            throw new IOException (e);
        } catch (InterruptedException e) {
            throw new IOException (e);
        }
    }

    public void unlockQuietly() {
        try {
            zk.delete(lockPath, -1);
            lockPath = null;
        } catch (Exception e) {
        }
    }

}
