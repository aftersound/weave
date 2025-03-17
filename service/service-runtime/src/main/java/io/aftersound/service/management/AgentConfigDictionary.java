package io.aftersound.service.management;

import io.aftersound.config.KeyFilters;
import io.aftersound.util.Dictionary;
import io.aftersound.util.Key;

import java.util.Collection;

public class AgentConfigDictionary extends Dictionary {

    public static final Key<Boolean> ENABLED = Key.of("enabled", Boolean.class)
            .bindDefaultValue(false);

    public static final Key<String> MANAGER = Key.of("manager", String.class);

    public static final Key<Long> HEARTBEAT_INTERVAL = Key.of("heartbeat.interval", Long.class)
            .bindDefaultValue(30000L);   // in milliseconds

    public static final Collection<Key<?>> KEYS;
    static {
        initAndLockKeys(AgentConfigDictionary.class);
        KEYS = getDeclaredKeys(AgentConfigDictionary.class, KeyFilters.ANY);
    }
}
