package io.aftersound.weave.codec;

public interface Codec<T> {
    Encoder<T> encoder();
    Decoder<T> decoder();
}
