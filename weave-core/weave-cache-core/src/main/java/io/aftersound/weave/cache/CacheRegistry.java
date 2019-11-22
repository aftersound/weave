package io.aftersound.weave.cache;

import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindings;

import java.util.HashMap;
import java.util.Map;

public class CacheRegistry {

    private final CacheFactoryRegistry cfr;
    private final Map<String, Cache> cacheById = new HashMap<>();

    public CacheRegistry(ActorBindings<CacheControl, CacheFactory<? extends CacheControl, ? extends Cache>, Cache> cacheFactoryBindings) {
        try {
            this.cfr = new CacheFactoryRegistry(this, cacheFactoryBindings).initialize();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeCache(String id, CacheControl cacheControl) throws Exception {
        cfr.getCacheFactory(cacheControl.getType()).create(id, cacheControl);
    }

    <CACHE extends Cache> void registerCache(String id, CACHE cache) {
        Cache previous = cacheById.put(id, cache);
        if (previous != null) {
            previous.invalidateAll();
        }
    }

    public void unregisterAndDestroyCache(String id) {
        Cache cache = cacheById.remove(id);
        if (cache != null) {
            cache.invalidateAll();
        }
    }

    public <CACHE extends Cache> CACHE getCache(String id) {
        return (CACHE)cacheById.get(id);
    }
}
