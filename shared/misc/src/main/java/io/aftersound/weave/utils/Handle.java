package io.aftersound.weave.utils;

import java.util.HashMap;
import java.util.Map;

public class Handle<T> {

    private static final Map<Class<?>, Handle<?>> INSTANCES = new HashMap<>();

    private final Class<T> type;
    private T obj;

    private boolean locked;

    private static final Object INSTANCE_LOCK = new Object();

    private Handle(Class<T> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public static <T> Handle<T> of(Class<T> type) {
        synchronized (INSTANCE_LOCK) {
            if (!INSTANCES.containsKey(type)) {
                INSTANCES.put(type, new Handle<>(type));
            }
        }
        return (Handle<T>) INSTANCES.get(type);
    }

    public synchronized void setAndLock(T obj) {
        if (!locked) {
            this.obj = obj;
        }
        this.locked = true;
    }

    public synchronized T get() {
        return obj;
    }

    public synchronized T remove() {
        T removed = obj;

        // remove handle
        INSTANCES.remove(type);
        obj = null;

        return obj;
    }

}
