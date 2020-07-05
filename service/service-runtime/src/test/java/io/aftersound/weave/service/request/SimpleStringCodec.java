package io.aftersound.weave.service.request;

import io.aftersound.weave.codec.Codec;
import io.aftersound.weave.codec.Decoder;
import io.aftersound.weave.codec.Encoder;

public class SimpleStringCodec implements Codec<String, String> {

    @Override
    public Encoder<String, String> encoder() {
        return new Encoder<String, String>() {
            @Override
            public String encode(String s) {
                return s;
            }
        };
    }

    @Override
    public Decoder<String, String> decoder() {
        return new Decoder<String, String>() {
            @Override
            public String decode(String s) {
                return s.toUpperCase();
            }
        };
    }

}
