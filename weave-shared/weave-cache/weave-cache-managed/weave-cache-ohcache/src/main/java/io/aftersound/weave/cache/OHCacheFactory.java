package io.aftersound.weave.cache;

import com.google.common.cache.Cache;
import io.aftersound.weave.common.NamedType;

public class OHCacheFactory extends CacheFactory<OHCacheControl, OHCache> {

    public static final NamedType<CacheControl> COMPANION_CONTROL_TYPE = OHCacheControl.TYPE;
    public static final NamedType<Cache> COMPANION_PRODUCT_TYPE = NamedType.of(
            OHCacheControl.TYPE.name(),
            OHCache.class
    );

    public OHCacheFactory(CacheRegistry cacheRegistry) {
        super(cacheRegistry);
    }

    @Override
    protected OHCache createCache(OHCacheControl control) {
        return new OHCache();
    }
}
