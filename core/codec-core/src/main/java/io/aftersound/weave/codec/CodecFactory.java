package io.aftersound.weave.codec;

public abstract class CodecFactory {

    public abstract String getType();

    public abstract <T> Codec<T> createCodec(String codecSpec);

    public final <T> Codec<T> createCodec(CodecControl codecControl) {
        return createCodec(codecControl.asCodecSpec());
    }

}
