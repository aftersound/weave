package io.aftersound.service.request;

import io.aftersound.func.AbstractFuncWithHints;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

class ParamReadFunc extends AbstractFuncWithHints<Map<String, ParamValueHolder>, Map<String, Object>> {

    private final List<String> sourceParameters;

    public ParamReadFunc(List<String> sourceParameters) {
        this.sourceParameters = sourceParameters;
    }

    @Override
    public Map<String, Object> apply(Map<String, ParamValueHolder> source) {
        if (sourceParameters == null || sourceParameters.isEmpty()) {
            return null;
        }

        Map<String, Object> m = new LinkedHashMap<>(sourceParameters.size());
        for (String sourceParameter : sourceParameters) {
            ParamValueHolder paramValueHolder = source.get(sourceParameter);
            m.put(sourceParameter, paramValueHolder != null ? paramValueHolder.getValue() : null);
        }
        return m;
    }

}
