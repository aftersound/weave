package io.aftersound.weave.service.rl;

import io.aftersound.util.Key;

/**
 * A container which holds rate limit evaluation decision
 */
public interface RateLimitDecision {

    enum Code {
        Serve,
        Block,
        SoftLimit
    }

    /**
     * @return the type name of rate limit handling mechanism
     */
    String getType();

    /**
     * @return the code of decision
     */
    Code getCode();

    /**
     * @return if this decision is to block the request
     */
    boolean isBlock();

    /**
     * @param key key of a piece of info
     * @param <T> generic type of the piece of info
     * @return a piece of auth ino associated with given key
     */
    <T> T get(Key<T> key);
}
