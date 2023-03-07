package io.aftersound.weave.hsqldb;

import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.BooleanParser;
import io.aftersound.weave.common.parser.IntegerParser;
import io.aftersound.weave.common.parser.StringParser;
import io.aftersound.weave.config.KeyFilters;

import java.util.Collection;

final class HSQLDBConfigDictionary extends Dictionary {

    public static final Key<String> SERVER_DATABASE = Key.of(
            "server.database.*",
            new StringParser()
    ).withPattern("server\\.database\\.\\w*");

    public static final Key<String> SERVER_DBNAME = Key.of(
            "server.dbname.*",
            new StringParser()
    ).withPattern("server\\.dbname\\.\\w*");

    public static final Key<Integer> SERVER_PORT = Key.of(
            "server.port",
            new IntegerParser().defaultValue(9001)
    );

    public static final Key<Boolean> SERVER_REMOTE_OPEN = Key.of(
            "server.remote_open",
            new BooleanParser().defaultValue(true)
    );

    public static final Collection<Key<?>> KEYS;
    static {
        lockDictionary(HSQLDBConfigDictionary.class);
        KEYS = getDeclaredKeys(HSQLDBConfigDictionary.class, KeyFilters.NOT_SECURITY_KEY);
    }
}
