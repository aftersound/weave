package io.aftersound.weave.client.cr;

import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.StringParser;
import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.config.KeyFilters;

import java.util.Collection;

class SettingsDictionary extends Dictionary {

    public static final Key<String> CLASS_NAME = Key.of(
            "class.name",
            new StringParser()
    ).markAsRequired();

    public static final Key<String> BASE_DIRECTORY = Key.of(
            "base.directory",
            new StringParser()
    );

    public static final Collection<Key<?>> CONFIG_KEYS;
    public static final Collection<Key<?>> SECURITY_KEYS;
    static {
        lockDictionary(SettingsDictionary.class);
        CONFIG_KEYS = getDeclaredKeys(SettingsDictionary.class, KeyFilters.NOT_SECURITY_KEY);
        SECURITY_KEYS = getDeclaredKeys(SettingsDictionary.class, KeyFilters.SECURITY_KEY);
    }

}
