package io.aftersound.config;

import io.aftersound.func.Directive;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.func.common.ObjectFuncFactory;
import io.aftersound.schema.Field;
import io.aftersound.schema.Schema;
import io.aftersound.util.Dictionary;
import io.aftersound.util.Key;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import static io.aftersound.config.KeyAttributes.*;

final class SampleDictionary extends Dictionary {

    public static final Key<byte[]> BASE64_ENCODED_BYTES_KEY = Key.of(
                    "base64.encoded.bytes.key",
                    byte[].class
            )
            .bindParseFunc(MasterFuncFactory.instance().create("CHAIN(MAP:GET(base64.encoded.bytes.key),BASE64:DECODE(String,Bytes))"));

    public static final Key<byte[]> BASE64_ENCODED_BYTES_KEY_W_DEFAULT = Key.of(
                    "base64.encoded.bytes.key.w.default",
                    byte[].class
            )
            .bindParseFunc(MasterFuncFactory.instance().create("CHAIN(MAP:GET(base64.encoded.bytes.key.w.default),BASE64:DECODE(String,Bytes))"))
            .bindDefaultValue("haha".getBytes());

    public static final Key<String> BASE64_ENCODED_STRING_KEY = Key.of(
                    "base64.encoded.string.key",
                    String.class)
            .bindParseFunc(MasterFuncFactory.instance().create("CHAIN(MAP:GET(base64.encoded.string.key),BASE64:DECODE(String,String))"));

    public static final Key<String> BASE64_ENCODED_STRING_KEY_W_DEFAULT = Key.of(
                    "base64.encoded.string.key.w.default",
                    String.class)
            .bindParseFunc(MasterFuncFactory.instance().create("CHAIN(MAP:GET(base64.encoded.string.key.w.default),BASE64:DECODE(String,String))"))
            .bindDefaultValue("haha");

    public static final Key<Boolean> BOOLEAN_KEY = Key.of("boolean.key", Boolean.class);

    public static final Key<Boolean> BOOLEAN_KEY_W_DEFAULT = Key.of("boolean.key.w.default", Boolean.class)
            .bindDefaultValue(Boolean.TRUE);

    public static final Key<Double> DOUBLE_KEY = Key.of("double.key", Double.class);

    public static final Key<Double> DOUBLE_KEY_W_DEFAULT = Key.of("double.key.w.default", Double.class)
            .bindDefaultValue(100d);

    public static final Key<Float> FLOAT_KEY = Key.of("float.key", Float.class);

    public static final Key<Float> FLOAT_KEY_W_DEFAULT = Key.of("float.key.w.default", Float.class)
            .bindDefaultValue(100f);

    public static final Key<Integer> INTEGER_KEY = Key.of("integer.key", Integer.class);

    public static final Key<Integer> INTEGER_KEY_W_DEFAULT = Key.of("integer.key.w.default", Integer.class)
            .bindDefaultValue(100);

    public static final Key<Long> LONG_KEY = Key.of("long.key", Long.class);

    public static final Key<Long> LONG_KEY_W_DEFAULT = Key.of("long.key.w.default", Long.class)
            .bindDefaultValue(100L);

    public static final Key<List<String>> STRING_LIST_KEY = Key.of("string.list.key")
            .bindParseFunc(MasterFuncFactory.instance().create("CHAIN(MAP:GET(string.list.key),STR:SPLIT(BASE64|LA==))"));

    public static final Key<List<String>> STRING_LIST_KEY_W_DEFAULT = Key.of("string.list.key.w.default")
            .bindDefaultValue(Arrays.asList("h", "a", "h", "a"))
            .bindParseFunc(MasterFuncFactory.instance().create("CHAIN(MAP:GET(string.list.key),STR:SPLIT(BASE64|LA==))"));

    public static final Key<String> STRING_KEY = Key.of("string.key", String.class);

    public static final Key<String> STRING_KEY_W_DEFAULT = Key.of("string.key.w.default", String.class)
            .bindDefaultValue("haha");

    public static final Key<Object> VOID_KEY = Key.of(
                    "void.key"
            )
            .bindParseFunc(MasterFuncFactory.instance().create("NULL()"));

    public static final Key<String> USER = Key.of("user", String.class)
            .withAttribute(PROTECTED, true)
            .withAttribute(REQUIRED, true);

    public static final Key<String> PASSWORD = Key.of("password", String.class)
            .withAttribute(SECRET, true)
            .withAttribute(REQUIRED, true);

    public static final Key<String> WILDCARD_KEY = Key.of("wildcard.*", String.class)
            .withAttribute(PATTERN, Pattern.compile("wildcard\\.\\w*"));

    public static final Key<FullName> FULL_NAME = Key.of("full.name", FullName.class)
            .bindParseFunc(
                    ObjectFuncFactory.createParseFromFunc(
                            Schema.of(
                                            "FullName",
                                            List.of(
                                                    Field.stringFieldBuilder("firstName")
                                                            .withDirective(
                                                                    Directive.of(
                                                                            "CVP",
                                                                            "CONFIG_VALUE_PARSE",
                                                                            "CHAIN(SpEL:EVAL(current),MAP:GET(first.name))"
                                                                    )
                                                            )
                                                            .build(),
                                                    Field.stringFieldBuilder("lastName")
                                                            .withDirective(
                                                                    Directive.of(
                                                                            "CVP",
                                                                            "CONFIG_VALUE_PARSE",
                                                                            "CHAIN(SpEL:EVAL(current),MAP:GET(last.name))"
                                                                    )
                                                            )
                                                            .build()
                                            )
                                    )
                                    .init(MasterFuncFactory.instance()),
                            FullName.class,
                            "CONFIG_VALUE_PARSE"
                    )
            )
            .withDescription("User's full name");

    public static final Collection<Key<?>> CONFIG_KEYS;
    public static final Collection<Key<?>> SECRET_KEYS;
    public static final Collection<Key<?>> KEYS_WITH_DEFAULT;
    public static final Collection<Key<?>> ALL_KEYS;
    static {
        initAndLockKeys(SampleDictionary.class);
        CONFIG_KEYS = getDeclaredKeys(SampleDictionary.class, key -> !key.hasAttribute(SECRET, true));
        SECRET_KEYS = getDeclaredKeys(SampleDictionary.class, KeyFilters.SECRET_KEY);
        KEYS_WITH_DEFAULT = getDeclaredKeys(SampleDictionary.class, key -> key.defaultValue() != null);
        ALL_KEYS = getDeclaredKeys(SampleDictionary.class, KeyFilters.ANY);
    }

}
