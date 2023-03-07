package io.aftersound.weave.job.runner;

import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.BooleanParser;
import io.aftersound.weave.common.parser.FirstRawKeyValueParser;
import io.aftersound.weave.common.parser.LongParser;
import io.aftersound.weave.common.parser.StringParser;
import io.aftersound.weave.config.KeyFilters;

import java.util.Collection;
import java.util.Map;

public class AgentConfigDictionary extends Dictionary {

    public static final Key<Map<String, String>> RUNNER_CAPABILITY = Key.of(
            "runner.capability",
            new FirstRawKeyValueParser<Map<String, String>>() {
                @Override
                protected Map<String, String> _parse(String rawValue) {
                    return Helper.parseAsMap(rawValue);
                }
            }
    ).markAsRequired();

    public static final Key<String> JOB_MANAGER = Key.of(
            "job.manager",
            new StringParser()
    ).markAsRequired();

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
