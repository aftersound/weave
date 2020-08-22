package io.aftersound.weave.common;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

public class DictionaryTest {

    private static class Dictionary1 extends Dictionary {
        public static final Key<String> K1 = Key.of("K1");
    }

    private static class Dictionary2 extends Dictionary1 {
        public static final Key<String> K2 = Key.of("K2");

        public static final Collection<Key<?>> KEYS;
        static {
            lockDictionary(Dictionary2.class);
            KEYS = getDeclaredKeys(Dictionary2.class, CommonKeyFilters.ANY);
        }
    }

    @Test
    public void testDictionary() {
        assertEquals(2, Dictionary2.KEYS.size());
    }

}