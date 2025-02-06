package io.aftersound.weave.hsqldb;

import io.aftersound.config.KeyFilters;
import io.aftersound.util.Dictionary;
import io.aftersound.util.Key;

import java.util.Collection;
import java.util.regex.Pattern;

import static io.aftersound.config.KeyAttributes.PATTERN;
import static io.aftersound.config.Parsers.*;

final class HSQLDBConfigDictionary extends Dictionary {

    public static final Key<String> SERVER_DATABASE = Key.of("server.database.*", String.class)
            .bindValueParser(STRING_PARSER)
            .set(PATTERN, Pattern.compile("server\\.database\\.\\w*"));

    public static final Key<String> SERVER_DBNAME = Key.of("server.dbname.*", String.class)
            .bindValueParser(STRING_PARSER)
            .set(PATTERN, Pattern.compile("server\\.dbname\\.\\w*"));

    public static final Key<Integer> SERVER_PORT = Key.of("server.port", Integer.class)
            .bindDefaultValue(9001)
            .bindValueParser(INTEGER_PARSER);

    public static final Key<Boolean> SERVER_REMOTE_OPEN = Key.of("server.remote_open", Boolean.class)
            .bindDefaultValue(true)
            .bindValueParser(BOOLEAN_PARSER);

    public static final Collection<Key<?>> KEYS;
    static {
        lockDictionary(HSQLDBConfigDictionary.class);
        KEYS = getDeclaredKeys(HSQLDBConfigDictionary.class, KeyFilters.NOT_SECURITY_KEY);
    }
}
