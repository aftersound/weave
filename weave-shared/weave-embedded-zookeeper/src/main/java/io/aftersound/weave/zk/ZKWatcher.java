package io.aftersound.weave.zk;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;

import java.util.HashMap;
import java.util.Map;

public class ZKWatcher implements Watcher {

    private final Map<Event.KeeperState, ZKWatchedEventActor> eventActors = new HashMap<>();

    public ZKWatcher withEventActor(Event.KeeperState keeperState, ZKWatchedEventActor eventActor) {
        this.eventActors.put(keeperState, eventActor);
        return this;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        ZKWatchedEventActor eventActor = eventActors.get(watchedEvent.getState());
        if (eventActor != null) {
            eventActor.act(watchedEvent);
        }
    }

}
