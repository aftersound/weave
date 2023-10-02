package io.aftersound.weave.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class MessageTest {

    @Test
    public void testConstructor() {
        assertNotNull(new Message());
        assertNotNull(new Message("400", "Bad Request"));
    }

    @Test
    public void testSetterGetter() {
        Message message = new Message();
        message.setId("400");
        message.setMessage("Bad Request");
        assertEquals("400", message.getId());
        assertEquals("Bad Request", message.getMessage());
    }



}