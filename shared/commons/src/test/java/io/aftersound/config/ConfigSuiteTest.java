package io.aftersound.config;

import io.aftersound.util.Key;
import io.aftersound.util.MapBuilder;
import org.junit.jupiter.api.Test;

import java.util.*;

import static io.aftersound.config.KeyAttributes.PROTECTED;
import static io.aftersound.config.KeyAttributes.SECRET;
import static io.aftersound.config.SampleDictionary.*;
import static org.junit.jupiter.api.Assertions.*;

public class ConfigSuiteTest {

    @Test
    public void testKeyTags() {
        assertTrue(PASSWORD.hasAttribute(SECRET, true));
        assertTrue(USER.hasAttribute(PROTECTED, true));
    }

    @Test
    public void testMissingRequired() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap().build();

        assertThrows(ConfigException.class, () -> ConfigUtils.extractConfig(configSource, SECRET_KEYS));
    }

    @Test
    public void testMissingRequiredExplicitly() {
        Map<String, Object> configSource = Collections.emptyMap();
        assertThrows(ConfigException.class, () -> ConfigUtils.extractConfig(configSource, CONFIG_KEYS));
    }

    @Test
    public void testDefaultBehavior() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap()
                .put("user", "user123")
                .put("password", "super_strong_password")
                .put("first.name", "World")
                .put("last.name", "Hello")
                .put("void.key", "something")
                .build();

        Config config = Config.from(configSource, ALL_KEYS);

        assertEquals("user123", config.v(USER));
        assertEquals("super_strong_password", config.v(PASSWORD));

        assertNull(config.v(BASE64_ENCODED_BYTES_KEY));
        assertEquals("haha", new String(config.v(BASE64_ENCODED_BYTES_KEY_W_DEFAULT)));

        assertNull(config.v(BASE64_ENCODED_STRING_KEY));
        assertEquals("haha", config.v(BASE64_ENCODED_STRING_KEY_W_DEFAULT));

        assertNull(config.v(BOOLEAN_KEY));
        assertTrue(config.v(BOOLEAN_KEY_W_DEFAULT));

        assertNull(config.v(DOUBLE_KEY));
        assertEquals(100D, config.v(DOUBLE_KEY_W_DEFAULT), 0.0d);

        assertNull(config.v(FLOAT_KEY));
        assertEquals(100F, config.v(FLOAT_KEY_W_DEFAULT), 0.0f);

        assertNull(config.v(INTEGER_KEY));
        assertEquals(100, config.v(INTEGER_KEY_W_DEFAULT).intValue());

        assertNull(config.v(LONG_KEY));
        assertEquals(100L, config.v(LONG_KEY_W_DEFAULT).longValue());

        assertNull(config.v(STRING_LIST_KEY));
        assertEquals("h", config.v(STRING_LIST_KEY_W_DEFAULT).get(0));
        assertEquals("a", config.v(STRING_LIST_KEY_W_DEFAULT).get(1));
        assertEquals("h", config.v(STRING_LIST_KEY_W_DEFAULT).get(2));
        assertEquals("a", config.v(STRING_LIST_KEY_W_DEFAULT).get(3));

        assertNull(config.v(STRING_KEY));
        assertEquals("haha", config.v(STRING_KEY_W_DEFAULT));

        assertNull(config.v(VOID_KEY));
    }

    @Test
    public void testNonDefaultBehavior() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap()
                .put("first.name", "World")
                .put("last.name", "Hello")
                .put("base64.encoded.bytes.key", "aGFoYQ==")
                .put("base64.encoded.string.key", "aGFoYQ==")
                .put("boolean.key", true)
                .put("double.key", 100.0d)
                .put("float.key", 100.0f)
                .put("integer.key", 100)
                .put("long.key", 100L)
                .put("string.list.key", "h,a,h,a")
                .put("string.key", "haha")
                .put("user", "user123")
                .build();

        Config config = Config.from(configSource, CONFIG_KEYS);

        assertEquals("haha", new String(config.v(BASE64_ENCODED_BYTES_KEY)));
        assertEquals("haha", config.v(BASE64_ENCODED_STRING_KEY));
        assertTrue(config.v(BOOLEAN_KEY));
        assertEquals(100D, config.v(DOUBLE_KEY), 0.0d);
        assertEquals(100F, config.v(FLOAT_KEY), 0.0f);
        assertEquals(100, config.v(INTEGER_KEY).intValue());
        assertEquals(100L, config.v(LONG_KEY).longValue());
        assertEquals("h", config.v(STRING_LIST_KEY).get(0));
        assertEquals("a", config.v(STRING_LIST_KEY).get(1));
        assertEquals("h", config.v(STRING_LIST_KEY).get(2));
        assertEquals("a", config.v(STRING_LIST_KEY).get(3));
        assertEquals("haha", config.v(STRING_KEY));
    }

    @Test
    public void testValueParserError() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap()
                .put("first.name", "World")
                .put("last.name", "Hello")
                .put("double.key", "notdouble")
                .put("float.key", "notfloat")
                .put("integer.key", "notinteger")
                .put("long.key", "notlong")
                .build();

        assertThrows(
                ConfigException.class, () -> Config.from(configSource, CONFIG_KEYS)
        );
    }

    @Test
    public void testCompositeKey() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap()
                .put("first.name", "World")
                .put("last.name", "Hello")
                .put("user", "user123")
                .build();

        Config config = Config.from(configSource, CONFIG_KEYS);

        assertEquals("User's full name", FULL_NAME.description());
        assertNotNull(config.v(FULL_NAME));
        assertEquals("World", config.v(FULL_NAME).getFirstName());
        assertEquals("Hello", config.v(FULL_NAME).getLastName());
    }

    @Test
    public void testWildcardConfig() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap()
                .put("first.name", "World")
                .put("last.name", "Hello")
                .put("wildcard.111", "NEW")
                .put("wildcard.222", "USED")
                .put("user", "user123")
                .build();

        Config config = Config.from(configSource, CONFIG_KEYS);
        assertEquals("NEW", config.v(Key.<String>of("wildcard.111")));
        assertEquals("USED", config.v(Key.<String>of("wildcard.222")));
    }

    @Test
    public void testSubconfig() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap()
                .put("first.name", "World")
                .put("last.name", "Hello")
                .put("user", "user123")
                .build();

        Config config = Config.from(configSource, CONFIG_KEYS);

        Config s = config.subconfig(KEYS_WITH_DEFAULT);

        assertEquals("haha", new String(s.v(BASE64_ENCODED_BYTES_KEY_W_DEFAULT)));
        assertEquals("haha", s.v(BASE64_ENCODED_STRING_KEY_W_DEFAULT));
        assertTrue(s.v(BOOLEAN_KEY_W_DEFAULT));
        assertEquals(100D, s.v(DOUBLE_KEY_W_DEFAULT), 0.0d);
        assertEquals(100F, s.v(FLOAT_KEY_W_DEFAULT), 0.0f);
        assertEquals(100, s.v(INTEGER_KEY_W_DEFAULT).intValue());
        assertEquals(100L, s.v(LONG_KEY_W_DEFAULT).longValue());
        assertEquals("h", s.v(STRING_LIST_KEY_W_DEFAULT).get(0));
        assertEquals("a", s.v(STRING_LIST_KEY_W_DEFAULT).get(1));
        assertEquals("h", s.v(STRING_LIST_KEY_W_DEFAULT).get(2));
        assertEquals("a", s.v(STRING_LIST_KEY_W_DEFAULT).get(3));
        assertEquals("haha", s.v(STRING_KEY_W_DEFAULT));
    }

    @Test
    public void testConfigAsMap() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap()
                .put("first.name", "World")
                .put("last.name", "Hello")
                .put("user", "user123")
                .build();

        Config config = Config.from(configSource, CONFIG_KEYS);
        Config s = config.subconfig(KEYS_WITH_DEFAULT);

        Map<String, Object> m = s.asMap();
        assertEquals("haha", new String((byte[])m.get(BASE64_ENCODED_BYTES_KEY_W_DEFAULT.name())));
        assertEquals("haha", m.get(BASE64_ENCODED_STRING_KEY_W_DEFAULT.name()));
        assertTrue((Boolean)m.get(BOOLEAN_KEY_W_DEFAULT.name()));
        assertEquals(100D, ((Double)m.get(DOUBLE_KEY_W_DEFAULT.name())), 0.0d);
        assertEquals(100F, ((Float)m.get(FLOAT_KEY_W_DEFAULT.name())), 0.0f);
        assertEquals(100, ((Integer)m.get(INTEGER_KEY_W_DEFAULT.name())).intValue());
        assertEquals(100L, ((Long)m.get(LONG_KEY_W_DEFAULT.name())).longValue());
        assertEquals("h", ((List<String>)m.get(STRING_LIST_KEY_W_DEFAULT.name())).get(0));
        assertEquals("a", ((List<String>)m.get(STRING_LIST_KEY_W_DEFAULT.name())).get(1));
        assertEquals("h", ((List<String>)m.get(STRING_LIST_KEY_W_DEFAULT.name())).get(2));
        assertEquals("a", ((List<String>)m.get(STRING_LIST_KEY_W_DEFAULT.name())).get(3));
        assertEquals("haha", m.get(STRING_KEY_W_DEFAULT.name()));
    }

    @Test
    public void testConfigAsMap1() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap()
                .put("first.name", "World")
                .put("last.name", "Hello")
                .put("user", "user123")
                .build();

        Config s = Config.from(configSource, CONFIG_KEYS).subconfig(KEYS_WITH_DEFAULT);

        Map<String, Object> m = s.asMap();
        assertNotNull(m.get(BASE64_ENCODED_BYTES_KEY_W_DEFAULT.name()));
        assertEquals("haha", m.get(BASE64_ENCODED_STRING_KEY_W_DEFAULT.name()));
        assertEquals(true, m.get(BOOLEAN_KEY_W_DEFAULT.name()));
        assertEquals(100.0d, m.get(DOUBLE_KEY_W_DEFAULT.name()));
        assertEquals(100.0f, m.get(FLOAT_KEY_W_DEFAULT.name()));
        assertEquals(100, m.get(INTEGER_KEY_W_DEFAULT.name()));
        assertEquals(100L, m.get(LONG_KEY_W_DEFAULT.name()));
        assertEquals(Arrays.asList("h", "a", "h", "a"), m.get(STRING_LIST_KEY_W_DEFAULT.name()));
        assertEquals("haha", m.get(STRING_KEY_W_DEFAULT.name()));
    }

    @Test
    public void testVRequired() {
        Map<String, Object> configSource = MapBuilder.<String, Object>hashMap()
                .put("string.key", "hello")
                .put("user", "user123")
                .build();
        Config cfg = Config.from(configSource, CONFIG_KEYS);
        cfg.vRequired(STRING_KEY);
    }

}