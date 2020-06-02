package io.aftersound.weave.utils;

/**
 * A conceptual factory which takes input and creates product
 * @param <INPUT>
 * @param <PRODUCT>
 */
public interface Factory<INPUT, PRODUCT> {
    PRODUCT create(INPUT input);
}
