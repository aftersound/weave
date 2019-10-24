package io.aftersound.weave.cache.guava;

import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.common.NamedType;

public class GuavaCacheControl implements CacheControl {

    public static final NamedType<CacheControl> TYPE = NamedType.of(
            "Guava",
            GuavaCacheControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private String id;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
