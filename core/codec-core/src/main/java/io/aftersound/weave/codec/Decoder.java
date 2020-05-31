package io.aftersound.weave.codec;

public interface Decoder<ENCODED, DECODED> {
    DECODED decode(ENCODED encoded);
}
