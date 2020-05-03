package io.aftersound.weave.config;

import io.aftersound.weave.config.parser.StringParser;
import org.junit.Test;

import java.util.Collections;

public class ValueParserTest {

    @Test(expected = IllegalStateException.class)
    public void testValueParserFirstRawKeyNull() {
        new StringParser().firstRawKey();
    }

    @Test(expected = IllegalStateException.class)
    public void testValueParserFirstRawKeyEmpty() {
        new StringParser().rawKeys(Collections.<String>emptyList()).firstRawKey();
    }

    @Test(expected = IllegalStateException.class)
    public void testValueParserRawKeysNull() {
        new StringParser().rawKeys();
    }

    @Test(expected = IllegalStateException.class)
    public void testValueParserRawKeysEmpty() {
        new StringParser().rawKeys(Collections.<String>emptyList()).rawKeys();
    }

}
