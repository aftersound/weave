package io.aftersound.weave.service.request;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.codec.CodecFactory;
import io.aftersound.weave.service.metadata.param.ParamField;

import java.util.List;

public class ParamValueParser {

    private static final ValueParserRegistry VALUE_PARSER_REGISTRY = new ValueParserRegistry();

    private final ActorRegistry<CodecFactory> codecFactoryRegistry;

    public ParamValueParser(ActorRegistry<CodecFactory> codecFactoryRegistry) {
        this.codecFactoryRegistry = codecFactoryRegistry;
    }

    public ParamValueHolder parse(ParamField paramField, String paramName, List<String> rawValues) {
        ValueParser valueParser = getValueParser(paramField.getValueSpec());
        if (paramField.isMultiValued()) {
            Object values = valueParser.parseMultiValues(rawValues);
            if (values != null) {
                return ParamValueHolder.multiValuedScoped(
                        paramField.getType().name(),
                        paramName,
                        paramField.getValueSpec(),
                        values
                ).bindRawValues(rawValues);
            }
        } else {
            Object value = valueParser.parseSingleValue(rawValues);
            if (value != null) {
                return ParamValueHolder.singleValuedScoped(
                        paramField.getType().name(),
                        paramName,
                        paramField.getValueSpec(),
                        value
                ).bindRawValues(rawValues);
            }
        }
        return null;
    }

    private ValueParser getValueParser(String valueType) {
        if (valueType == null) {
            return VoidValueParser.INSTANCE;
        }
        return VALUE_PARSER_REGISTRY.getValueParser(valueType, codecFactoryRegistry);
    }

}
