package io.aftersound.service.cache;

public abstract class AbstractCacheControl implements CacheControl{

    private KeyControl keyControl;

    @Override
    public final KeyControl getKeyControl() {
        return keyControl;
    }

    public final void setKeyControl(KeyControl keyControl) {
        this.keyControl = keyControl;
    }

}
