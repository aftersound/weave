package io.aftersound.weave.hikari3x;

import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.BooleanParser;
import io.aftersound.weave.common.parser.IntegerParser;
import io.aftersound.weave.common.parser.LongParser;
import io.aftersound.weave.common.parser.StringParser;
import io.aftersound.weave.config.KeyFilters;

import java.util.Collection;

import static io.aftersound.weave.config.Tags.SECURITY;

final class HikariDataSourceConfigDictionary extends Dictionary {

    public static final Key<String> POOL_NAME = Key.of(
            "pool.name",
            new StringParser()
    );

    public static final Key<String> CATALOG = Key.of(
            "catalog",
            new StringParser()
    );

    public static final Key<String> DRIVER_CLASS_NAME = Key.of(
            "driver.class.name",
            new StringParser()
    ).markAsRequired();

    public static final Key<String> JDBC_URL = Key.of(
            "jdbc.url",
            new StringParser()
    ).markAsRequired();

    public static final Key<Integer> MAXIMUM_POOL_SIZE = Key.of(
            "maximum.pool.size",
            new IntegerParser().defaultValue(5)
    );

    public static final Key<Integer> MINIMUM_IDLE = Key.of(
            "minimum.idle",
            new IntegerParser().defaultValue(1)
    );

    public static final Key<Long> CONNECTION_TIMEOUT = Key.of(
            "connection.timeout",
            new LongParser().defaultValue(10000L)
    );

    public static final Key<Long> IDLE_TIMEOUT = Key.of(
            "idle.timeout",
            new LongParser().defaultValue(600000L)
    );

    public static final Key<Long> MAX_LIFE_TIME = Key.of(
            "max.life.time",
            new LongParser().defaultValue(1800000L)
    );

    public static final Key<Long> LEAK_DETECTION_THRESHOLD = Key.of(
            "leak.detection.threshold",
            new LongParser().defaultValue(30000L)
    );

    public static final Key<Boolean> AUTO_COMMIT = Key.of(
            "auto.commit",
            new BooleanParser().defaultValue(true)
    );

    public static final Key<String> CONNECTION_TEST_QUERY = Key.of(
            "connection.test.query",
            new StringParser()
    );

    public static final Key<String> USERNAME = Key.of(
            "username",
            new StringParser()
    ).withTag(SECURITY);

    public static final Key<String> PASSWORD = Key.of(
            "password",
            new StringParser()
    ).withTag(SECURITY);

    public static final Collection<Key<?>> KEYS;
    static {
        lockDictionary(HikariDataSourceConfigDictionary.class);
        KEYS = getDeclaredKeys(HikariDataSourceConfigDictionary.class, KeyFilters.ANY);
    }
}
