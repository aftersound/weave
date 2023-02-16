package io.aftersound.weave.utils;

import java.util.Collection;
import java.util.Map;

/**
 * A conceptual repository which holds key-values
 */
public interface KeyValueRepository {
    void insert(String key, byte[] value) throws Exception;

    byte[] get(String key) throws Exception;

    void update(String key, byte[] value) throws Exception;

    void delete(String key) throws Exception;

    void bulkInsert(Map<String, byte[]> kvs) throws Exception;

    Map<String, byte[]> bulkGet(Collection<String> keys) throws Exception;

    void bulkUpdate(Map<String, byte[]> kvs) throws Exception;

    void bulkDelete(Collection<String> keys) throws Exception;
}
