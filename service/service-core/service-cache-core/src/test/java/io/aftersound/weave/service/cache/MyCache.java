package io.aftersound.weave.service.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;

public class MyCache implements Cache {

    @Override
    public Object getIfPresent(Object key) {
        return null;
    }

    @Override
    public Object get(Object key, Callable valueLoader) throws ExecutionException {
        return null;
    }

    @Override
    public ImmutableMap getAllPresent(Iterable keys) {
        return null;
    }

    @Override
    public void put(Object key, Object value) {

    }

    @Override
    public void putAll(Map m) {
    }

    @Override
    public void invalidate(Object key) {

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
    public ConcurrentMap asMap() {
        return null;
    }

    @Override
    public void cleanUp() {
    }


    @Override
    public boolean equals(Object object) {
        return false;
    }

    @Override
    public void invalidateAll(Iterable keys) {

    }
}