package io.aftersound.weave.client.fs;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.StringParser;
import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.config.KeyFilters;

import java.util.Collection;

public class SettingsDictionary extends Dictionary {

    public static final Key<String> BASE_DIRECTORY = Key.of(
            "base.directory",
            new StringParser().defaultValue("/")
    );

    public static final Collection<Key<?>> CONFIG_KEYS;
    public static final Collection<Key<?>> SECURITY_KEYS;
    static {
        lockDictionary(SettingsDictionary.class);
        CONFIG_KEYS = getDeclaredKeys(SettingsDictionary.class, KeyFilters.NOT_SECURITY_KEY);
        SECURITY_KEYS = getDeclaredKeys(SettingsDictionary.class, KeyFilters.SECURITY_KEY);
    }

}
