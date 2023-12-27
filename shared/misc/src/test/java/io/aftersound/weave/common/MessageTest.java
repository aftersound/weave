package io.aftersound.weave.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessageTest {

    @Test
    public void testBuilder1() {
        Message m = Message.builder()
                .withCode("001")
                .withContent("Hello")
                .withSeverity(Severity.Info)
                .withCategory("Application")
                .build();
        assertEquals("001", m.getCode());
        assertEquals("Hello", m.getContent());
        assertEquals(Severity.Info, m.getSeverity());
        assertEquals("Application", m.getCategory());
    }

    @Test
    public void testBuilder2() {
        Message m = Message.builder("001", "Hello")
                .withSeverity(Severity.Info)
                .withCategory("Application")
                .build();
        assertEquals("001", m.getCode());
        assertEquals("Hello", m.getContent());
        assertEquals(Severity.Info, m.getSeverity());
        assertEquals("Application", m.getCategory());
    }

    @Test
    public void testWarning() {
        Message m = new Message();
        m.setCode("002");
        m.setContent("Warning!!!");
        m.setSeverity(Severity.Warning);
        m.setCategory("Application");
        assertEquals("002", m.getCode());
        assertEquals("Warning!!!", m.getContent());
        assertEquals(Severity.Warning, m.getSeverity());
        assertEquals("Application", m.getCategory());
    }

    @Test
    public void testError() {
        Message m = new Message();
        m.setCode("003");
        m.setContent("Error!!!");
        m.setSeverity(Severity.Warning);
        m.setCategory("Application");
        assertEquals("003", m.getCode());
        assertEquals("Error!!!", m.getContent());
        assertEquals(Severity.Warning, m.getSeverity());
        assertEquals("Application", m.getCategory());
    }

}