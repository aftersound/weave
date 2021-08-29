package io.aftersound.weave.service.request;

import io.aftersound.weave.common.ValueFunc;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ParamReadFunc implements ValueFunc<Map<String, ParamValueHolder>, Object> {

    private final List<String> sourceParameters;

    public ParamReadFunc(List<String> sourceParameters) {
        this.sourceParameters = sourceParameters;
    }

    @Override
    public Object process(Map<String, ParamValueHolder> source) {
        if (sourceParameters == null || sourceParameters.isEmpty()) {
            return null;
        }

        if (sourceParameters.size() == 1) {
            ParamValueHolder paramValueHolder = source.get(sourceParameters.get(0));
            return paramValueHolder != null ? paramValueHolder.getValue() : null;
        }

        Map<String, Object> m = new LinkedHashMap<>(sourceParameters.size());
        for (String sourceParameter : sourceParameters) {
            ParamValueHolder paramValueHolder = source.get(sourceParameter);
            m.put(sourceParameter, paramValueHolder != null ? paramValueHolder.getValue() : null);
        }
        return m;
    }

}
