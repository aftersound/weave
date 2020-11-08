package io.aftersound.weave.utils;

/**
 * A conceptual factory which takes input and creates product
 * @param <INPUT>
 *          input for the factory
 * @param <PRODUCT>
 *          product of the factory
 */
public interface Factory<INPUT, PRODUCT> {
    PRODUCT create(INPUT input);
}
