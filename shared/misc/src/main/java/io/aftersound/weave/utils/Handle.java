package io.aftersound.weave.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * A handle which holds object of singleton or multi-ton and provides access to it.
 * @param <T>
 *          generic type of objects being handled
 */
public class Handle<T> {

    private static final Map<String, Handle<?>> INSTANCES = new HashMap<>();
    private static final Object INSTANCE_LOCK = new Object();

    private final String fullIdentifier;
    private final Class<T> type;

    private T obj;

    private boolean locked;

    private Handle(String fullIdentifier, Class<T> type) {
        this.fullIdentifier = fullIdentifier;
        this.type = type;
    }

    /**
     * return existing handle or create a handle of given type and id
     * @param id
     *          identifier, uniquely identify a handle together with type
     * @param type
     *          type of object to be handled
     * @param <T>
     *          generic type of object to be handled
     * @return
     *          a handle associated with given id and type
     */
    @SuppressWarnings("unchecked")
    public static <T> Handle<T> of(String id, Class<T> type) {
        String fullIdentifier = type.getName() + "_" + id;
        synchronized (INSTANCE_LOCK) {
            if (!INSTANCES.containsKey(fullIdentifier)) {
                INSTANCES.put(fullIdentifier, new Handle<>(fullIdentifier, type));
            }
        }
        return (Handle<T>) INSTANCES.get(fullIdentifier);
    }

    /**
     * Bind the object once and for all
     * @param obj
     *          the object being handled
     */
    public synchronized Handle<T> setAndLock(T obj) {
        if (!locked) {
            this.obj = obj;
        }
        this.locked = true;
        return this;
    }

    /**
     * @return
     *          the object being handled
     */
    public synchronized T get() {
        return obj;
    }

    /**
     * detach this handle from the object
     * @return
     *          the object
     */
    public synchronized T detach() {
        T removed = obj;

        // remove handle
        INSTANCES.remove(fullIdentifier);
        obj = null;

        return obj;
    }

}
