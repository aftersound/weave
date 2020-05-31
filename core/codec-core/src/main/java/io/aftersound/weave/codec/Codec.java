package io.aftersound.weave.codec;

public interface Codec<SOURCE,ENCODED> {
    Encoder<SOURCE,ENCODED> encoder();
    Decoder<ENCODED,SOURCE> decoder();
}
