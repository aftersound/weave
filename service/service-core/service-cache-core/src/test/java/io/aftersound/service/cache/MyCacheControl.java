package io.aftersound.service.cache;

import io.aftersound.common.NamedType;

public class MyCacheControl implements CacheControl {

    static final NamedType<CacheControl> TYPE = NamedType.of("MyCache", MyCacheControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

    private KeyControl keyControl;

    @Override
    public KeyControl getKeyControl() {
        return keyControl;
    }

    public void setKeyControl(KeyControl keyControl) {
        this.keyControl = keyControl;
    }
}
