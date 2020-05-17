package io.aftersound.weave.client;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertSame;

public class ClientHandleTest {

    @Test
    public void testClient() {
        Object obj = new Object();
        Endpoint endpoint = Endpoint.of(
                "test",
                "test",
                Collections.<String, String>emptyMap()
        );
        assertSame(obj, ClientHandle.of(obj, endpoint).client());
    }

}