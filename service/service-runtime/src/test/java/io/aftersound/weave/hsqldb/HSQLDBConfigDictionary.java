package io.aftersound.weave.hsqldb;

import io.aftersound.config.KeyAttributes;
import io.aftersound.config.KeyFilters;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.util.Dictionary;
import io.aftersound.util.Key;

import java.util.Collection;
import java.util.regex.Pattern;

import static io.aftersound.config.KeyAttributes.PATTERN;

final class HSQLDBConfigDictionary extends Dictionary {

    public static final Key<String> SERVER_DATABASE = Key.of("server.database.*", String.class)
            .withAttribute(PATTERN, Pattern.compile("server\\.database\\.\\w*"))
            .withAttribute(KeyAttributes.FUNC_FACTORY, MasterFuncFactory.instance());

    public static final Key<String> SERVER_DBNAME = Key.of("server.dbname.*", String.class)
            .withAttribute(PATTERN, Pattern.compile("server\\.dbname\\.\\w*"))
            .withAttribute(KeyAttributes.FUNC_FACTORY, MasterFuncFactory.instance());

    public static final Key<Integer> SERVER_PORT = Key.of("server.port", Integer.class);

    public static final Key<Boolean> SERVER_REMOTE_OPEN = Key.of("server.remote_open", Boolean.class)
            .bindDefaultValue(true);

    public static final Collection<Key<?>> KEYS;
    static {
        initAndLockKeys(HSQLDBConfigDictionary.class);
        KEYS = getDeclaredKeys(HSQLDBConfigDictionary.class, KeyFilters.NON_SECRET_KEY);
    }
}
