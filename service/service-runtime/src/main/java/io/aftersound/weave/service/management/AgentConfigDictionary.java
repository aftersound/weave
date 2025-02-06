package io.aftersound.weave.service.management;

import io.aftersound.config.KeyFilters;
import io.aftersound.util.Dictionary;
import io.aftersound.util.Key;

import java.util.Collection;

import static io.aftersound.config.Parsers.*;

public class AgentConfigDictionary extends Dictionary {

    public static final Key<Boolean> ENABLED = Key.of("enabled", Boolean.class)
            .bindDefaultValue(false)
            .bindValueParser(BOOLEAN_PARSER);

    public static final Key<String> MANAGER = Key.of("manager", String.class)
            .bindValueParser(STRING_PARSER);

    public static final Key<Long> HEARTBEAT_INTERVAL = Key.of("heartbeat.interval", Long.class)
            .bindDefaultValue(30000L)   // in milliseconds
            .bindValueParser(LONG_PARSER);

    public static final Collection<Key<?>> KEYS;
    static {
        lockDictionary(AgentConfigDictionary.class);
        KEYS = getDeclaredKeys(AgentConfigDictionary.class, KeyFilters.ANY);
    }
}
