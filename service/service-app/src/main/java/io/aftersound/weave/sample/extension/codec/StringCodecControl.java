package io.aftersound.weave.sample.extension.codec;

import io.aftersound.weave.codec.CodecControl;
import io.aftersound.weave.common.NamedType;

public class StringCodecControl implements CodecControl {

    static final NamedType<CodecControl> TYPE = NamedType.of(
            "String",
            StringCodecControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    @Override
    public String asCodecSpec() {
        return TYPE.name();
    }

}
