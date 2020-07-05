package io.aftersound.weave.service.request;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.codec.Codec;
import io.aftersound.weave.codec.CodecFactory;
import io.aftersound.weave.utils.Factory;
import io.aftersound.weave.utils.Registry;

final class ValueParserRegistry extends Registry<String, ValueParser> {

    public ValueParser getValueParser(String valueSpec, ActorRegistry<CodecFactory> codecFactoryRegistry) {
        return get(
                valueSpec,
                new Factory<String, ValueParser>() {
                    @Override
                    public ValueParser create(String valueSpec) {
                        Codec<?, String> codec = Codec.REGISTRY.get().getCodec(valueSpec, codecFactoryRegistry);
                        if (codec != null) {
                            return new CodecAwareValueParser(codec.decoder());
                        } else {
                            return VoidValueParser.INSTANCE;
                        }
                    }
                }
        );
    }
}
