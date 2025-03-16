package io.aftersound.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NamedTypeTest {

    @Test
    public void testConstructor() {
        NamedType<String> namedType = NamedType.of("test", String.class);
        assertNotNull(namedType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullName() {
        NamedType<String> namedType = NamedType.of(null, String.class);
        assertNotNull(namedType);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorNullType() {
        NamedType<String> namedType = NamedType.of("test", null);
        assertNotNull(namedType);
    }

    @Test
    public void testGetters() {
        NamedType<String> namedType = NamedType.of("test", String.class);
        assertEquals("test", namedType.name());
        assertEquals(String.class, namedType.type());
    }
}
