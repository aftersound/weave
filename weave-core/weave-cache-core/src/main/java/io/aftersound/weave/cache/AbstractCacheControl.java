package io.aftersound.weave.cache;

public abstract class AbstractCacheControl implements CacheControl{

    private String id;
    private KeyControl keyControl;

    @Override
    public final String getId() {
        return id;
    }

    public final void setId(String id) {
        this.id = id;
    }

    @Override
    public final KeyControl getKeyControl() {
        return keyControl;
    }

    public final void setKeyControl(KeyControl keyControl) {
        this.keyControl = keyControl;
    }

}
