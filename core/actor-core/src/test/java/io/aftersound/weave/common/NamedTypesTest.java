package io.aftersound.weave.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class NamedTypesTest {

    @Test
    public void testAll() {
        NamedTypes<String> namedTypes = new NamedTypes<>();
        NamedType<String> nt1 = NamedType.of("s1", String.class);
        namedTypes.include(nt1);
        NamedType<String> nt2 = NamedType.of("s2", String.class);
        namedTypes.include(nt2);
        assertEquals(2, namedTypes.all().size());
    }
}
