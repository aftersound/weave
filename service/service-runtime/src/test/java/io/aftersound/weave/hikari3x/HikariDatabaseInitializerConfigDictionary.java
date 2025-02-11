package io.aftersound.weave.hikari3x;

import io.aftersound.config.KeyFilters;
import io.aftersound.util.Dictionary;
import io.aftersound.util.Key;

import java.util.Collection;

import static io.aftersound.config.KeyAttributes.REQUIRED;
import static io.aftersound.config.KeyAttributes.SECRET;

final class HikariDatabaseInitializerConfigDictionary extends Dictionary {

    public static final Key<String> POOL_NAME = Key.of("pool.name", String.class);

    public static final Key<String> CATALOG = Key.of("catalog", String.class);

    public static final Key<String> DRIVER_CLASS_NAME = Key.of("driver.class.name", String.class)
            .withAttribute(REQUIRED, true);

    public static final Key<String> JDBC_URL = Key.of("jdbc.url", String.class)
            .withAttribute(REQUIRED, true);

    public static final Key<Integer> MAXIMUM_POOL_SIZE = Key.of("maximum.pool.size", Integer.class)
            .bindDefaultValue(5);

    public static final Key<Integer> MINIMUM_IDLE = Key.of("minimum.idle", Integer.class)
            .bindDefaultValue(1);

    public static final Key<Long> CONNECTION_TIMEOUT = Key.of("connection.timeout", Long.class)
            .bindDefaultValue(10000L);

    public static final Key<Long> IDLE_TIMEOUT = Key.of("idle.timeout", Long.class)
            .bindDefaultValue(600000L);

    public static final Key<Long> MAX_LIFE_TIME = Key.of("max.life.time", Long.class)
            .bindDefaultValue(1800000L);

    public static final Key<Long> LEAK_DETECTION_THRESHOLD = Key.of("leak.detection.threshold", Long.class)
            .bindDefaultValue(30000L);

    public static final Key<Boolean> AUTO_COMMIT = Key.of("auto.commit", Boolean.class)
            .bindDefaultValue(true);

    public static final Key<String> CONNECTION_TEST_QUERY = Key.of("connection.test.query", String.class);

    public static final Key<String> USERNAME = Key.of("username", String.class)
            .withAttribute(SECRET, true);

    public static final Key<String> PASSWORD = Key.of("password", String.class)
            .withAttribute(SECRET, true);

    public static final Key<String> INIT_SCRIPT = Key.of("init.script", String.class)
            .bindParseFunc(FUNC_FACTORY.create("CHAIN(MAP:GET(init.script),BASE64:DECODE(String,String))"));

    public static final Collection<Key<?>> KEYS;
    static {
        initAndLockKeys(HikariDatabaseInitializerConfigDictionary.class);
        KEYS = getDeclaredKeys(HikariDatabaseInitializerConfigDictionary.class, KeyFilters.ANY);
    }
}
