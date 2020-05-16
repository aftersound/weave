package io.aftersound.weave.service.cache;

import com.google.common.cache.Cache;
import io.aftersound.weave.common.NamedType;

public class MyCacheFactory extends CacheFactory<MyCacheControl, MyCache> {

    public static final NamedType<CacheControl> COMPANION_CONTROL_TYPE = NamedType.of("MyCache", MyCacheControl.class);
    public static final NamedType<Cache> COMPANION_PRODUCT_TYPE = NamedType.of("MyCache", MyCache.class);

    public MyCacheFactory(CacheRegistry cacheRegistry) {
        super(cacheRegistry);
    }

    @Override
    protected MyCache createCache(MyCacheControl control) {
        return new MyCache();
    }
}
