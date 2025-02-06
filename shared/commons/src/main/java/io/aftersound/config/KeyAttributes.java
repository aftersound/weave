package io.aftersound.config;

import io.aftersound.util.Key;

import java.util.regex.Pattern;

public class KeyAttributes {
    public static final Key<Pattern> PATTERN = Key.of("pattern", Pattern.class);
    public static final Key<String[]> RAW_KEYS = Key.of("rawKeys", String[].class);
    public static final Key<Boolean> REQUIRED = Key.of("required", Boolean.class);
    public static final Key<Boolean> SECURITY = Key.of("_security_", Boolean.class);
}
