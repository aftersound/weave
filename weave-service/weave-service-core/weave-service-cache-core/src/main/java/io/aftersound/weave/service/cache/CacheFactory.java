package io.aftersound.weave.service.cache;

import com.google.common.cache.Cache;

public abstract class CacheFactory<CONTROL extends CacheControl, CACHE extends Cache> {

    protected final CacheRegistry cacheRegistry;

    private Object lock = new Object();

    protected CacheFactory(CacheRegistry cacheRegistry) {
        this.cacheRegistry = cacheRegistry;
    }

    public final CACHE create(String id, CONTROL control) {
        synchronized (lock) {
            CACHE cache = createCache(control);
            cacheRegistry.registerCache(id, cache);
            return cache;
        }
    }

    protected abstract CACHE createCache(CONTROL control);
}
