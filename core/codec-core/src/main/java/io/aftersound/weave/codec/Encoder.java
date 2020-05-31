package io.aftersound.weave.codec;

public interface Encoder<SOURCE,ENCODED> {
    ENCODED encode(SOURCE source);
}
