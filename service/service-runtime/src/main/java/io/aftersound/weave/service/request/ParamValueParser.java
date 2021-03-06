package io.aftersound.weave.service.request;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.common.ValueFunc;
import io.aftersound.weave.common.ValueFuncFactory;
import io.aftersound.weave.common.ValueFuncRegistry;
import io.aftersound.weave.service.metadata.param.ParamField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParamValueParser {

    private final ValueFuncRegistry valueFuncRegistry;

    public ParamValueParser(ActorRegistry<ValueFuncFactory> valueFuncFactoryRegistry) {
        this.valueFuncRegistry = new ValueFuncRegistry(valueFuncFactoryRegistry);
    }

    public ParamValueHolder parse(ParamField paramField, String paramName, List<String> rawValues) {
        String valueFuncSpec = paramField.getValueFuncSpec();
        if (valueFuncSpec == null) {
            valueFuncSpec = "_";    // default spec as PassThroughFunc
        }
        ValueFunc<String, ?> valueFunc = valueFuncRegistry.getValueFunc(valueFuncSpec);
        if (paramField.isMultiValued()) {
            List<Object> values = null;
            try {
                values = parseMultiValues(valueFunc, rawValues);
            } catch (Exception e) {
                // TODO: log error or not?
            }
            if (values != null) {
                return ParamValueHolder.multiValuedScoped(
                        paramField.getParamType().name(),
                        paramName,
                        paramField.getType(),
                        values
                ).bindRawValues(rawValues);
            }
        } else {
            Object value = null;
            try {
                value = parseSingleValue(valueFunc, rawValues);
            } catch (Exception e) {
                // TODO: log error or not?
            }
            if (value != null) {
                return ParamValueHolder.singleValuedScoped(
                        paramField.getParamType().name(),
                        paramName,
                        paramField.getType(),
                        value
                ).bindRawValues(rawValues);
            }
        }
        return null;
    }

    private List<Object> parseMultiValues(ValueFunc<String,?> valueFunc, List<String> rawValues) {
        if (rawValues == null || rawValues.isEmpty()) {
            return Collections.emptyList();
        }

        List<Object> parsed = new ArrayList<>();
        for (String rawValue : rawValues) {
            parsed.add(valueFunc.process(rawValue));
        }
        return parsed;
    }

    private Object parseSingleValue(ValueFunc<String,?> valueFunc, List<String> rawValues) {
        if (rawValues != null && !rawValues.isEmpty()) {
            return valueFunc.process(rawValues.get(0));
        } else {
            return valueFunc.process(null);
        }
    }

}
