package io.aftersound.weave.cache.guava;

import com.google.common.cache.Cache;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.cache.CacheFactory;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.common.NamedType;

public class GuavaCacheFactory extends CacheFactory<GuavaCacheControl, Cache> {

    public static final NamedType<CacheControl> COMPANION_CONTROL_TYPE = GuavaCacheControl.TYPE;
    public static final NamedType<Cache> COMPANION_PRODUCT_TYPE = NamedType.of(
            GuavaCacheControl.TYPE.name(),
            Cache.class
    );

    public GuavaCacheFactory(CacheRegistry cacheRegistry) {
        super(cacheRegistry);
    }

    @Override
    public Cache createCache(GuavaCacheControl control) {
        return null;
    }
}
