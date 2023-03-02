package io.aftersound.weave.service.management;

import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.BooleanParser;
import io.aftersound.weave.common.parser.LongParser;
import io.aftersound.weave.common.parser.StringParser;
import io.aftersound.weave.config.KeyFilters;

import java.util.Collection;

public class AgentConfigDictionary extends Dictionary {

    public static final Key<Boolean> ENABLED = Key.of(
            "enabled",
            new BooleanParser().defaultValue(Boolean.FALSE)
    );

    public static final Key<String> MANAGER = Key.of(
            "manager",
            new StringParser()
    );

    public static final Key<Long> HEARTBEAT_INTERVAL = Key.of(
            "heartbeat.interval",
            new LongParser().defaultValue(30000L)   // in milliseconds
    );

    public static final Collection<Key<?>> KEYS;
    static {
        lockDictionary(AgentConfigDictionary.class);
        KEYS = getDeclaredKeys(AgentConfigDictionary.class, KeyFilters.ANY);
    }
}
