package io.aftersound.weave.codec;

public interface Encoder<T> {
    byte[] encodeAsBytes(T object);
    String encodeAsString(T object);
}
