package io.aftersound.weave.cache;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

public class OHCache<K, V> implements Cache<K, V> {


    @Nullable
    @Override
    public V getIfPresent(K key) {
        return null;
    }

    @Override
    public V get(K key, Callable<? extends V> valueLoader) throws ExecutionException {
        return null;
    }

    @Override
    public ImmutableMap<K, V> getAllPresent(Iterable<? extends K> keys) {
        return null;
    }

    @Override
    public void put(K key, V value) {

    }

    @Override
    public void invalidate(Object key) {

    }

    @Override
    public void invalidateAll(Iterable<?> keys) {

    }

    @Override
    public void invalidateAll() {

    }

    @Override
    public long size() {
        return 0;
    }

    @Override
    public CacheStats stats() {
        return null;
    }

    @Override
    public ConcurrentMap<K, V> asMap() {
        return null;
    }

    @Override
    public void cleanUp() {

    }

    @Override
    public V get(K key) throws ExecutionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public V getUnchecked(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V apply(K key) {
        throw new UnsupportedOperationException();
    }
}
