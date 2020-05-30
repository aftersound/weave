package io.aftersound.weave.codec;

public interface Decoder<T> {
    T decode(byte[] encoded);
    T decode(String encoded);
}
