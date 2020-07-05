package io.aftersound.weave.service.request;

import io.aftersound.weave.codec.Codec;
import io.aftersound.weave.codec.CodecControl;
import io.aftersound.weave.codec.CodecFactory;
import io.aftersound.weave.common.NamedType;

public class SimpleStringCodecFactory extends CodecFactory {

    public static final NamedType<CodecControl> COMPANION_CONTROL_TYPE = SimpleStringCodecControl.TYPE;

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public <S, E> Codec<S, E> createCodec(String codecSpec) {
        return (Codec<S, E>) new SimpleStringCodec();
    }
}
