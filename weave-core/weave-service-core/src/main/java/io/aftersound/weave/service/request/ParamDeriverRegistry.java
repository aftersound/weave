package io.aftersound.weave.service.request;

import java.util.HashMap;
import java.util.Map;

public class ParamDeriverRegistry {

    private final Map<String, Deriver> deriverByTypeName = new HashMap<>();

    public ParamDeriverRegistry(Map<String, Deriver> deriverByTypeName) {
        if (deriverByTypeName != null) {
            this.deriverByTypeName.putAll(deriverByTypeName);
        }
    }

    public Deriver getDeriver(String typeName) {
        return deriverByTypeName.get(typeName);
    }

}
