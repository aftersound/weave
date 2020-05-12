package io.aftersound.weave.service.cache;

import com.google.common.cache.Cache;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.common.NamedTypes;

import java.util.HashMap;
import java.util.Map;

public class CacheFactoryRegistry {

    private final Object lock = new Object();

    private final CacheRegistry cacheRegistry;
    private final ActorBindings<CacheControl, CacheFactory<? extends CacheControl, ? extends Cache>, Cache> cacheFactoryBindings;

    private Map<String, CacheFactory<?, ?>> factoryByCacheTypeName = new HashMap<>();

    CacheFactoryRegistry(
            CacheRegistry cacheRegistry,
            ActorBindings<CacheControl, CacheFactory<? extends CacheControl, ? extends Cache>, Cache> cacheFactoryBindings) {
        this.cacheRegistry = cacheRegistry;
        this.cacheFactoryBindings = cacheFactoryBindings;
    }

    public CacheFactoryRegistry initialize() throws Exception {
        synchronized (lock) {
            NamedTypes<CacheControl> controlTypes = cacheFactoryBindings.controlTypes();
            for (String name : controlTypes.names()) {
                Class<CacheFactory<?, ?>> factoryType = (Class<CacheFactory<?, ?>>)cacheFactoryBindings.getActorType(name);
                CacheFactory<?, ?> factory = createFactory0(factoryType);
                factoryByCacheTypeName.put(name, factory);
            }
        }
        return this;
    }

    private CacheFactory<?,?> createFactory0(Class<CacheFactory<?,?>> factoryType) throws Exception {
        return factoryType.getDeclaredConstructor(CacheRegistry.class).newInstance(cacheRegistry);
    }

    <CACHE_CONTROL extends CacheControl,
            CACHE extends Cache,
            FACTORY extends CacheFactory<CACHE_CONTROL, CACHE>> FACTORY getCacheFactory(String name) throws Exception {
        return (FACTORY) factoryByCacheTypeName.get(name);
    }
}
