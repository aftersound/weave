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
        message.setCode("400");
        message.setContent("Bad Request");
        assertEquals("400", message.getCode());
        assertEquals("Bad Request", message.getContent());
    }



}