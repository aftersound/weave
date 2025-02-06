package io.aftersound.weave.hikari3x;

import io.aftersound.config.KeyFilters;
import io.aftersound.util.Dictionary;
import io.aftersound.util.Key;

import java.util.Collection;

import static io.aftersound.config.KeyAttributes.REQUIRED;
import static io.aftersound.config.KeyAttributes.SECURITY;
import static io.aftersound.config.Parsers.*;

final class HikariDatabaseInitializerConfigDictionary extends Dictionary {

    public static final Key<String> POOL_NAME = Key.of("pool.name", String.class)
            .bindValueParser(STRING_PARSER);

    public static final Key<String> CATALOG = Key.of("catalog", String.class)
            .bindValueParser(STRING_PARSER);

    public static final Key<String> DRIVER_CLASS_NAME = Key.of("driver.class.name", String.class)
            .bindValueParser(STRING_PARSER)
            .set(REQUIRED, true);

    public static final Key<String> JDBC_URL = Key.of("jdbc.url", String.class)
            .bindValueParser(STRING_PARSER)
            .set(REQUIRED, true);

    public static final Key<Integer> MAXIMUM_POOL_SIZE = Key.of("maximum.pool.size", Integer.class)
            .bindDefaultValue(5)
            .bindValueParser(INTEGER_PARSER);

    public static final Key<Integer> MINIMUM_IDLE = Key.of("minimum.idle", Integer.class)
            .bindDefaultValue(1)
            .bindValueParser(INTEGER_PARSER);

    public static final Key<Long> CONNECTION_TIMEOUT = Key.of("connection.timeout", Long.class)
            .bindDefaultValue(10000L)
            .bindValueParser(LONG_PARSER);

    public static final Key<Long> IDLE_TIMEOUT = Key.of("idle.timeout", Long.class)
            .bindDefaultValue(600000L)
            .bindValueParser(LONG_PARSER);

    public static final Key<Long> MAX_LIFE_TIME = Key.of("max.life.time", Long.class)
            .bindDefaultValue(1800000L)
            .bindValueParser(LONG_PARSER);

    public static final Key<Long> LEAK_DETECTION_THRESHOLD = Key.of("leak.detection.threshold", Long.class)
            .bindDefaultValue(30000L)
            .bindValueParser(LONG_PARSER);

    public static final Key<Boolean> AUTO_COMMIT = Key.of("auto.commit", Boolean.class)
            .bindDefaultValue(true)
            .bindValueParser(BOOLEAN_PARSER);

    public static final Key<String> CONNECTION_TEST_QUERY = Key.of("connection.test.query", String.class)
            .bindValueParser(STRING_PARSER);

    public static final Key<String> USERNAME = Key.of("username", String.class)
            .bindValueParser(STRING_PARSER)
            .set(SECURITY, true);

    public static final Key<String> PASSWORD = Key.of("password", String.class)
            .bindValueParser(STRING_PARSER)
            .set(SECURITY, true);

    public static final Key<String> INIT_SCRIPT = Key.of("init.script", String.class)
            .bindValueParser(BASE64_DECODER);

    public static final Collection<Key<?>> KEYS;
    static {
        lockDictionary(HikariDatabaseInitializerConfigDictionary.class);
        KEYS = getDeclaredKeys(HikariDatabaseInitializerConfigDictionary.class, KeyFilters.ANY);
    }
}
