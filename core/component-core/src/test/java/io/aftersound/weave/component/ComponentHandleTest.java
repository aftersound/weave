package io.aftersound.weave.component;

import io.aftersound.weave.common.Key;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertSame;

public class ComponentHandleTest {

    @Test
    public void testComponentHandle() {
        Object obj = new Object();
        ComponentConfig component = SimpleComponentConfig.of(
                "test",
                "test",
                Collections.<String, String>emptyMap()
        );
        assertSame(
                obj,
                ComponentHandle.of(
                        obj,
                        component,
                        new Signature() {

                            @Override
                            public boolean match(Signature another) {
                                return false;
                            }

                        },
                        Collections.<Key<?>>emptyList()).component()
        );
    }

}