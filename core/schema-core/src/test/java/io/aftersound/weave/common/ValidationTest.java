package io.aftersound.weave.common;

import org.junit.Test;

import static org.junit.Assert.*;

public class ValidationTest {

    @Test
    public void testConstructor() {
        assertNotNull(new Validation());
        assertNotNull(
                new Validation("MAP:HAS_KEY(firstName)", new Message("001", "Missing firstName"))
        );
    }

    @Test
    public void testSetterGetter() {
        Validation validation = new Validation();
        validation.setCondition("MAP:HAS_KEY(firstName)");
        validation.setMessage(new Message("001", "Missing firstName"));
        assertEquals("MAP:HAS_KEY(firstName)", validation.getCondition());
        assertNotNull(validation.getMessage());
    }

}