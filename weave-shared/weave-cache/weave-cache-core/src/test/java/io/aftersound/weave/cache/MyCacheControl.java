package io.aftersound.weave.cache;

import io.aftersound.weave.common.NamedType;

public class MyCacheControl implements CacheControl {

    static final NamedType<CacheControl> TYPE = NamedType.of("MyCache", MyCacheControl.class);

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
