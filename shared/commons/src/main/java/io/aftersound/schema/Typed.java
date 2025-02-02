package io.aftersound.schema;

/**
 * Type carrier
 * @param <T> carried type in generic form
 */
public interface Typed<T> {

    /**
     * @return the carried type
     */
    Class<T> type();

    /**
     * cast input value, return typed value if match or null if not match
     * @param v the value to be casted
     * @return casted value
     */
    T cast(Object v);

}
