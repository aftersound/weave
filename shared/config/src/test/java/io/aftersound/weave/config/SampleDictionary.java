package io.aftersound.weave.config;

import io.aftersound.weave.common.Dictionary;
import io.aftersound.weave.common.Key;
import io.aftersound.weave.common.parser.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

final class SampleDictionary extends Dictionary {

    private static final String WITH_DEFAULT = "WithDefault";

    public static final Key<byte[]> BASE64_ENCODED_BYTES_KEY = Key.of(
            "base64.encoded.bytes.key",
            new Base64EncodedBytesParser()
    );

    public static final Key<byte[]> BASE64_ENCODED_BYTES_KEY_W_DEFAULT = Key.of(
            "base64.encoded.bytes.key.w.default",
            new Base64EncodedBytesParser().defaultValue("haha".getBytes())
    ).withTag(WITH_DEFAULT);

    public static final Key<String> BASE64_ENCODED_STRING_KEY = Key.of(
            "base64.encoded.string.key",
            new Base64EncodedStringParser()
    );

    public static final Key<String> BASE64_ENCODED_STRING_KEY_W_DEFAULT = Key.of(
            "base64.encoded.string.key.w.default",
            new Base64EncodedStringParser().defaultValue("haha")
    ).withTag(WITH_DEFAULT);

    public static final Key<Boolean> BOOLEAN_KEY = Key.of(
            "boolean.key",
            new BooleanParser()
    );

    public static final Key<Boolean> BOOLEAN_KEY_W_DEFAULT = Key.of(
            "boolean.key.w.default",
            new BooleanParser().defaultValue(true)
    ).withTag(WITH_DEFAULT);

    public static final Key<Double> DOUBLE_KEY = Key.of(
            "double.key",
            new DoubleParser()
    );

    public static final Key<Double> DOUBLE_KEY_W_DEFAULT = Key.of(
            "double.key.w.default",
            new DoubleParser().defaultValue(100d)
    ).withTag(WITH_DEFAULT);

    public static final Key<Float> FLOAT_KEY = Key.of(
            "float.key",
            new FloatParser()
    );

    public static final Key<Float> FLOAT_KEY_W_DEFAULT = Key.of(
            "float.key.w.default",
            new FloatParser().defaultValue(100f)
    ).withTag(WITH_DEFAULT);

    public static final Key<Integer> INTEGER_KEY = Key.of(
            "integer.key",
            new IntegerParser()
    );

    public static final Key<Integer> INTEGER_KEY_W_DEFAULT = Key.of(
            "integer.key.w.default",
            new IntegerParser().defaultValue(100)
    ).withTag(WITH_DEFAULT);

    public static final Key<Long> LONG_KEY = Key.of(
            "long.key",
            new LongParser()
    );

    public static final Key<Long> LONG_KEY_W_DEFAULT = Key.of(
            "long.key.w.default",
            new LongParser().defaultValue(100L)
    ).withTag(WITH_DEFAULT);

    public static final Key<List<String>> STRING_LIST_KEY = Key.of(
            "string.list.key",
            new StringListParser(",")
    );

    public static final Key<List<String>> STRING_LIST_KEY_W_DEFAULT = Key.of(
            "string.list.key.w.default",
            new StringListParser(",").defaultValue(Arrays.asList("h", "a", "h", "a"))
    ).withTag(WITH_DEFAULT);

    public static final Key<String> STRING_KEY = Key.of(
            "string.key",
            new StringParser()
    );

    public static final Key<String> STRING_KEY_W_DEFAULT = Key.of(
            "string.key.w.default",
            new StringParser().defaultValue("haha")
    ).withTag(WITH_DEFAULT);

    public static final Key<Object> VOID_KEY = Key.of(
            "void.key"
    );

    public static final Key<String> USER = Key.of(
            "user",
            new StringParser()
    ).markAsRequired().withTag(Tags.SECURITY);

    public static final Key<String> PASSWORD = Key.of(
            "password",
            new StringParser()
    ).markAsRequired().withTag(Tags.SECURITY).withTag(Tags.PROTECTED);

    public static final Key<FullName> FULL_NAME = Key.of(
            "full.name",
            Arrays.asList(
                    "first.name",
                    "last.name"
            ),
            new FullNameParser()
    ).description("User's full name");

    public static final Collection<Key<?>> CONFIG_KEYS;
    public static final Collection<Key<?>> SECURITY_KEYS;
    public static final Collection<Key<?>> KEYS_WITH_DEFAULT;
    public static final Collection<Key<?>> ALL_KEYS;
    static {
        lockDictionary(SampleDictionary.class);
        CONFIG_KEYS = getDeclaredKeys(SampleDictionary.class, KeyFilters.NOT_SECURITY_KEY);
        SECURITY_KEYS = getDeclaredKeys(SampleDictionary.class, KeyFilters.SECURITY_KEY);
        KEYS_WITH_DEFAULT = getDeclaredKeys(SampleDictionary.class, KeyFilters.keyWithTag(WITH_DEFAULT));
        ALL_KEYS = getDeclaredKeys(SampleDictionary.class, KeyFilters.ANY);
    }

}
