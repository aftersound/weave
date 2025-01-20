package io.aftersound.util.map;

import io.aftersound.util.AttributeHolder;

public class Directives extends AttributeHolder<Directives> {

    public Directives() {
        super();
    }

    public Directives acquire(Directives others) {
        if (others != null) {
            super.acquire(others);
        }
        return this;
    }

}
