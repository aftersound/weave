package io.aftersound.weave.job.runner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.jackson.ObjectMapperBuilder;

import java.util.Collections;
import java.util.Map;

public class Helper {

    public static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private static final TypeReference<Map<String, String>> TYPE_REF_MAP = new TypeReference<Map<String, String>>() {};

    private static final TypeReference<Map<String, Object>> TYPE_REF_MAP1 = new TypeReference<Map<String, Object>>() {};

    public static String toJson(Map<String, String> m) {
        if (m != null) {
            try {
                return MAPPER.writeValueAsString(m);
            } catch (Exception e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Map<String, String> parseAsMap(String json) {
        try {
            return Helper.MAPPER.readValue(json, TYPE_REF_MAP);
        } catch (Exception e) {
            return null;
        }
    }

    public static Map<String, Object> parseAsMap1(String json) {
        try {
            return Helper.MAPPER.readValue(json, TYPE_REF_MAP1);
        } catch (Exception e) {
            return null;
        }
    }

}
