package io.aftersound.weave.codec;

public abstract class CodecFactory {

    public abstract String getType();

    public abstract <S,E> Codec<S,E> createCodec(String codecSpec);

    public final <S,E> Codec<S,E> createCodec(CodecControl codecControl) {
        return createCodec(codecControl.asCodecSpec());
    }

}
