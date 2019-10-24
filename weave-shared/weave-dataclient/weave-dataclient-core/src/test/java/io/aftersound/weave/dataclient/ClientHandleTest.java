package io.aftersound.weave.dataclient;

import org.junit.Test;

import static org.junit.Assert.assertSame;

public class ClientHandleTest {

    @Test
    public void testClient() {
        Object obj = new Object();
        assertSame(obj, ClientHandle.of(obj).client());
    }

}