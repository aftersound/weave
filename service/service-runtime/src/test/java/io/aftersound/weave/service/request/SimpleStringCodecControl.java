package io.aftersound.weave.service.request;

import io.aftersound.weave.codec.CodecControl;
import io.aftersound.weave.common.NamedType;

public class SimpleStringCodecControl implements CodecControl {

    public static final NamedType<CodecControl> TYPE = NamedType.of(
            "SimpleString",
            SimpleStringCodecControl.class
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
