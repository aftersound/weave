package io.aftersound.weave.service.metadata.param;

import io.aftersound.weave.metadata.Control;

public abstract class DeriveControl implements Control {

    private String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

}
