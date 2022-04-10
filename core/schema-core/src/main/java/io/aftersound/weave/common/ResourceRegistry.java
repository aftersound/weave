package io.aftersound.weave.common;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resource registry which holds resources to be referenced by {@link ValueFunc}
 */
public final class ResourceRegistry implements Serializable {

    private static Map<String, Object> resourceById = new ConcurrentHashMap<>();

    public static <T> void register(String id, T obj) {
        resourceById.put(id, obj);
    }

    public static <T> T get(String id) {
        return (T) (id != null ? resourceById.get(id) : null);
    }

}

