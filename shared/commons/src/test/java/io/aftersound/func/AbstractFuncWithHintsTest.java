package io.aftersound.func;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AbstractFuncWithHintsTest {

    @Test
    public void constructor() {
        AbstractFuncWithHints<Object, Object> func = new AbstractFuncWithHints<>() {
            @Override
            public Object apply(Object o) {
                return null;
            }
        };

        assertNotNull(func);
        assertFalse(func.hasHint("ON", "TARGET"));
    }

}