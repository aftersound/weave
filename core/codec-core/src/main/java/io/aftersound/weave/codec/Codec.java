package io.aftersound.weave.codec;

import io.aftersound.weave.utils.Handle;

public interface Codec<SOURCE,ENCODED> {

    public static final Handle<CodecRegistry> REGISTRY = Handle.of(
            "CodecRegistry",
            CodecRegistry.class
    ).setAndLock(new CodecRegistry());

    Encoder<SOURCE,ENCODED> encoder();
    Decoder<ENCODED,SOURCE> decoder();
}
