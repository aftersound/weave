package io.aftersound.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A registry which holds registered entries of (key, value) pairs
 *
 * @param <KEY>   - the type of the keys of the entries
 * @param <VALUE> - the type of the values of the entries
 */
public class Registry<KEY, VALUE> {

    private final Map<KEY, VALUE> map = new HashMap<>();
    private final Object lock = new Object();

    /**
     * Return the KEYed value if exists, or else use given factory
     * to create new VALUE, bind new VALUE with KEY then return
     *
     * @param key     - the key of the value
     * @param factory - the factory can take KEY as input and create a VALUE
     * @return a VALUE associated with given KEY
     */
    protected final VALUE get(KEY key, Function<KEY, VALUE> factory) {
        VALUE value = map.get(key);
        if (value != null) {
            return value;
        }

        synchronized (lock) {
            value = map.get(key);
            if (value == null) {
                value = factory.apply(key);
                map.put(key, value);
            }
        }
        return value;
    }

    /**
     * Return the KEYed VALUE(s) if exists, or else use given factory
     * to create new VALUES.
     *
     * @param keys    - keys of VALUES of interested
     * @param factory - the factory can take keys as input and create values
     * @return VALUE(s), each associated with one of given KEY
     */
    protected final Map<KEY, VALUE> get(Collection<KEY> keys, Function<KEY, VALUE> factory) {
        Map<KEY, VALUE> keyedValues = new HashMap<>();
        for (KEY key : keys) {
            keyedValues.put(key, get(key, factory));
        }
        return keyedValues;
    }

}
