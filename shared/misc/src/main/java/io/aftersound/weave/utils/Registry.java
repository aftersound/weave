package io.aftersound.weave.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A registry which holds objects and makes them available to accessor
 * @param <KEY>
 * @param <VALUE>
 */
public class Registry<KEY, VALUE> {

    private final Map<KEY, VALUE> map = new HashMap<>();
    private final Object lock = new Object();

    /**
     * return KEYed VALUE if exists, or else use {@link Factory} to create new VALUE,
     * bind new VALUE with KEY then return
     * @param key
     *          key of the value
     * @param factory
     *          a {@link Factory} could take KEY as input and create a VALUE
     * @return
     *          a VALUE associated with given KEY
     */
    protected final VALUE get(KEY key, Factory<KEY, VALUE> factory) {
        VALUE value = map.get(key);
        if (value != null) {
            return value;
        }

        synchronized (lock) {
            value = map.get(key);
            if (value == null) {
                value = factory.create(key);
                map.put(key, value);
            }
        }
        return value;
    }

    /**
     * return KEYed VALUE(s) if exist, or else use {@link Factory} to create new VALUES
     * @param keys
     *          keys of VALUES of interested
     * @param factory
     *          a {@link Factory} could take KEY as input and create a VALUE
     * @return
     *          VALUE(s), each associated with one of given KEY(s)
     */
    protected final Map<KEY, VALUE> get(Collection<KEY> keys, Factory<KEY, VALUE> factory) {
        Map<KEY, VALUE> keyedValues = new HashMap<>();
        for (KEY key : keys) {
            keyedValues.put(key, get(key, factory));
        }
        return keyedValues;
    }

}
