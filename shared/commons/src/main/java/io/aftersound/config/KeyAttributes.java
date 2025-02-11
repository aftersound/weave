package io.aftersound.config;

import io.aftersound.func.FuncFactory;
import io.aftersound.util.Key;

import java.util.regex.Pattern;

public class KeyAttributes {
    public static final Key<FuncFactory> FUNC_FACTORY = Key.of("funcFactory", FuncFactory.class);
    public static final Key<Pattern> PATTERN = Key.of("pattern", Pattern.class);
    public static final Key<Boolean> REQUIRED = Key.of("required", Boolean.class);
    public static final Key<Boolean> SECRET = Key.of("secret", Boolean.class);
    public static final Key<Boolean> PROTECTED = Key.of("protected", Boolean.class);
}
