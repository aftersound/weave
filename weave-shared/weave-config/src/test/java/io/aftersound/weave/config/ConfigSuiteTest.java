package io.aftersound.weave.config;

import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.aftersound.weave.config.SampleDictionary.*;
import static org.junit.Assert.*;

public class ConfigSuiteTest {

    @Test
    public void testKeyTags() {
        assertTrue(PASSWORD.hasTag(Tags.SECURITY));
        assertTrue(PASSWORD.hasTag(Tags.PROTECTED));
    }

    @Test(expected = ConfigException.class)
    public void testMissingRequired() {
        Map<String, String> configSource = new MapBuilder().build();

        Map<String, Object> config = new HashMap<>();
        config.putAll(ConfigUtils.extractConfig(configSource, SampleDictionary.SECURITY_KEYS));
    }

    @Test(expected = ConfigException.class)
    public void testMissingRequiredExplicitly() {
        Map<String, String> configSource = new MapBuilder().build();

        Map<String, Object> config = new HashMap<>();
        config.putAll(ConfigUtils.extractConfig(configSource, SampleDictionary.CONFIG_KEYS));
    }

    @Test
    public void testDefaultBehavior() {
        Map<String, String> configSource = new MapBuilder()
                .kv("user", "user123")
                .kv("password", "super_strong_password")
                .kv("first.name", "World")
                .kv("last.name", "Hello")
                .kv("void.key", "something")
                .build();

        Map<String, Object> config = new HashMap<>();
        config.putAll(ConfigUtils.extractConfig(configSource, SampleDictionary.CONFIG_KEYS));
        config.putAll(ConfigUtils.extractConfig(configSource, SampleDictionary.SECURITY_KEYS));

        Settings settings = Settings.from(config);

        assertEquals("user123", settings.v(USER));
        assertEquals("super_strong_password", settings.v(PASSWORD));

        assertNull(settings.v(BASE64_ENCODED_BYTES_KEY));
        assertEquals("haha", new String(settings.v(BASE64_ENCODED_BYTES_KEY_W_DEFAULT)));

        assertNull(settings.v(BASE64_ENCODED_STRING_KEY));
        assertEquals("haha", settings.v(BASE64_ENCODED_STRING_KEY_W_DEFAULT));

        assertNull(settings.v(BOOLEAN_KEY));
        assertTrue(settings.v(BOOLEAN_KEY_W_DEFAULT));

        assertNull(settings.v(DOUBLE_KEY));
        assertEquals(100D, settings.v(DOUBLE_KEY_W_DEFAULT).doubleValue(), 0.0d);

        assertNull(settings.v(FLOAT_KEY));
        assertEquals(100F, settings.v(FLOAT_KEY_W_DEFAULT).floatValue(), 0.0f);

        assertNull(settings.v(INTEGER_KEY));
        assertEquals(100, settings.v(INTEGER_KEY_W_DEFAULT).intValue());

        assertNull(settings.v(LONG_KEY));
        assertEquals(100L, settings.v(LONG_KEY_W_DEFAULT).longValue());

        assertNull(settings.v(STRING_LIST_KEY));
        assertEquals("h", settings.v(STRING_LIST_KEY_W_DEFAULT).get(0));
        assertEquals("a", settings.v(STRING_LIST_KEY_W_DEFAULT).get(1));
        assertEquals("h", settings.v(STRING_LIST_KEY_W_DEFAULT).get(2));
        assertEquals("a", settings.v(STRING_LIST_KEY_W_DEFAULT).get(3));

        assertNull(settings.v(STRING_KEY));
        assertEquals("haha", settings.v(STRING_KEY_W_DEFAULT));

        assertNull(settings.v(VOID_KEY));
    }

    @Test
    public void testNonDefaultBehavior() {
        Map<String, String> configSource = new MapBuilder()
                .kv("first.name", "World")
                .kv("last.name", "Hello")
                .kv("base64.encoded.bytes.key", "aGFoYQ==")
                .kv("base64.encoded.string.key", "aGFoYQ==")
                .kv("boolean.key", "true")
                .kv("double.key", "100.0")
                .kv("float.key", "100.0")
                .kv("integer.key", "100")
                .kv("long.key", "100")
                .kv("string.list.key", "h,a,h,a")
                .kv("string.key", "haha")
                .build();

        Map<String, Object> config = new HashMap<>();
        config.putAll(ConfigUtils.extractConfig(configSource, SampleDictionary.CONFIG_KEYS));

        Settings s = Settings.from(config);

        assertEquals("haha", new String(s.v(BASE64_ENCODED_BYTES_KEY)));
        assertEquals("haha", s.v(BASE64_ENCODED_STRING_KEY));
        assertTrue(s.v(BOOLEAN_KEY));
        assertEquals(100D, s.v(DOUBLE_KEY).doubleValue(), 0.0d);
        assertEquals(100F, s.v(FLOAT_KEY).floatValue(), 0.0f);
        assertEquals(100, s.v(INTEGER_KEY).intValue());
        assertEquals(100L, s.v(LONG_KEY).longValue());
        assertEquals("h", s.v(STRING_LIST_KEY).get(0));
        assertEquals("a", s.v(STRING_LIST_KEY).get(1));
        assertEquals("h", s.v(STRING_LIST_KEY).get(2));
        assertEquals("a", s.v(STRING_LIST_KEY).get(3));
        assertEquals("haha", s.v(STRING_KEY));
    }

    @Test
    public void testValueParserError() {
        Map<String, String> configSource = new MapBuilder()
                .kv("first.name", "World")
                .kv("last.name", "Hello")
                .kv("double.key", "notdouble")
                .kv("float.key", "notfloat")
                .kv("integer.key", "notinteger")
                .kv("long.key", "notlong")
                .build();

        Map<String, Object> config = new HashMap<>();
        config.putAll(ConfigUtils.extractConfig(configSource, SampleDictionary.CONFIG_KEYS));

        Settings s = Settings.from(config);

        assertNull(s.v(DOUBLE_KEY));
        assertNull(s.v(FLOAT_KEY));
        assertNull(s.v(INTEGER_KEY));
        assertNull(s.v(LONG_KEY));
    }

    @Test
    public void testCompositeKey() {
        Map<String, String> configSource = new MapBuilder()
                .kv("first.name", "World")
                .kv("last.name", "Hello")
                .build();

        Map<String, Object> config = new HashMap<>();
        config.putAll(ConfigUtils.extractConfig(configSource, SampleDictionary.CONFIG_KEYS));

        Settings settings = Settings.from(config);

        assertEquals("User's full name", FULL_NAME.description());
        assertNotNull(settings.v(FULL_NAME));
        assertEquals("World", settings.v(FULL_NAME).firstName());
        assertEquals("Hello", settings.v(FULL_NAME).lastName());
    }

    @Test
    public void testSubsettings() {
        Map<String, String> configSource = new MapBuilder()
                .kv("first.name", "World")
                .kv("last.name", "Hello")
                .build();

        Map<String, Object> config = ConfigUtils.extractConfig(
                configSource,
                SampleDictionary.CONFIG_KEYS
        );

        Settings s = Settings.from(config).subsettings(KEYS_WITH_DEFAULT);

        assertEquals("haha", new String(s.v(BASE64_ENCODED_BYTES_KEY_W_DEFAULT)));
        assertEquals("haha", s.v(BASE64_ENCODED_STRING_KEY_W_DEFAULT));
        assertTrue(s.v(BOOLEAN_KEY_W_DEFAULT));
        assertEquals(100D, s.v(DOUBLE_KEY_W_DEFAULT).doubleValue(), 0.0d);
        assertEquals(100F, s.v(FLOAT_KEY_W_DEFAULT).floatValue(), 0.0f);
        assertEquals(100, s.v(INTEGER_KEY_W_DEFAULT).intValue());
        assertEquals(100L, s.v(LONG_KEY_W_DEFAULT).longValue());
        assertEquals("h", s.v(STRING_LIST_KEY_W_DEFAULT).get(0));
        assertEquals("a", s.v(STRING_LIST_KEY_W_DEFAULT).get(1));
        assertEquals("h", s.v(STRING_LIST_KEY_W_DEFAULT).get(2));
        assertEquals("a", s.v(STRING_LIST_KEY_W_DEFAULT).get(3));
        assertEquals("haha", s.v(STRING_KEY_W_DEFAULT));
    }

    @Test
    public void testSettingsAsMap() {
        Map<String, String> configSource = new MapBuilder()
                .kv("first.name", "World")
                .kv("last.name", "Hello")
                .build();

        Map<String, Object> config = ConfigUtils.extractConfig(
                configSource,
                SampleDictionary.CONFIG_KEYS
        );
        Settings s = Settings.from(config).subsettings(KEYS_WITH_DEFAULT);

        Map<String, Object> m = s.asMap();
        assertEquals("haha", new String((byte[])m.get(BASE64_ENCODED_BYTES_KEY_W_DEFAULT.name())));
        assertEquals("haha", m.get(BASE64_ENCODED_STRING_KEY_W_DEFAULT.name()));
        assertTrue((Boolean)m.get(BOOLEAN_KEY_W_DEFAULT.name()));
        assertEquals(100D, ((Double)m.get(DOUBLE_KEY_W_DEFAULT.name())).doubleValue(), 0.0d);
        assertEquals(100F, ((Float)m.get(FLOAT_KEY_W_DEFAULT.name())).floatValue(), 0.0f);
        assertEquals(100, ((Integer)m.get(INTEGER_KEY_W_DEFAULT.name())).intValue());
        assertEquals(100L, ((Long)m.get(LONG_KEY_W_DEFAULT.name())).longValue());
        assertEquals("h", ((List<String>)m.get(STRING_LIST_KEY_W_DEFAULT.name())).get(0));
        assertEquals("a", ((List<String>)m.get(STRING_LIST_KEY_W_DEFAULT.name())).get(1));
        assertEquals("h", ((List<String>)m.get(STRING_LIST_KEY_W_DEFAULT.name())).get(2));
        assertEquals("a", ((List<String>)m.get(STRING_LIST_KEY_W_DEFAULT.name())).get(3));
        assertEquals("haha", m.get(STRING_KEY_W_DEFAULT.name()));
    }

    @Test
    public void testSettingsAsMap1() {
        Map<String, String> configSource = new MapBuilder()
                .kv("first.name", "World")
                .kv("last.name", "Hello")
                .build();

        Map<String, Object> config = ConfigUtils.extractConfig(
                configSource,
                SampleDictionary.CONFIG_KEYS
        );
        Settings s = Settings.from(config).subsettings(KEYS_WITH_DEFAULT);

        Map<String, String> m = s.asMap1();
        assertNotNull(m.get(BASE64_ENCODED_BYTES_KEY_W_DEFAULT.name()));
        assertEquals("haha", m.get(BASE64_ENCODED_STRING_KEY_W_DEFAULT.name()));
        assertEquals("true", m.get(BOOLEAN_KEY_W_DEFAULT.name()));
        assertEquals("100.0", m.get(DOUBLE_KEY_W_DEFAULT.name()));
        assertEquals("100.0", m.get(FLOAT_KEY_W_DEFAULT.name()));
        assertEquals("100", m.get(INTEGER_KEY_W_DEFAULT.name()));
        assertEquals("100", m.get(LONG_KEY_W_DEFAULT.name()));
        assertEquals("[h, a, h, a]", m.get(STRING_LIST_KEY_W_DEFAULT.name()));
        assertEquals("haha", m.get(STRING_KEY_W_DEFAULT.name()));
    }

}