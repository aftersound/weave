package io.aftersound.service.security;

import io.aftersound.util.Key;

/**
 * A container which holds evaluation result of authenticator/authorizer
 */
public interface Auth {

    /**
     * @return type of auth
     */
    String getType();

    /**
     * @return the name of the principal
     */
    String getName();

    /**
     * @param key key of a piece of info
     * @param <T> generic type of the piece of info
     * @return a piece of auth ino associated with given key
     */
    <T> T get(Key<T> key);
}
