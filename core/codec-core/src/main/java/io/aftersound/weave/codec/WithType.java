package io.aftersound.weave.codec;

/**
 * Implement this interface if a {@link Codec} is type aware
 * @param <T>
 *          generic type of sourc entity
 */
public interface WithType<T> {
    Class<T> getType();
}
