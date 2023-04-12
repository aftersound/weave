package io.aftersound.weave.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ContentHandleTest {

    @Test
    public void contentHandle() throws Exception {
        // BASE64 encoded content
        assertEquals("hello", new String(ContentHandle.of("BASE64|aGVsbG8=").get()));
    }

}
