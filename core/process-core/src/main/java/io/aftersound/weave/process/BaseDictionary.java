package io.aftersound.weave.process;

import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.StringParser;

public abstract class BaseDictionary extends Dictionary {
    public static final Key<String> OP = Key.of("op", new StringParser());
}
