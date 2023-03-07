package io.aftersound.weave.job.runner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.jackson.ObjectMapperBuilder;

import java.util.Collections;
import java.util.Map;

public class Helper {

    public static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private static final TypeReference<Map<String, String>> TYPE_REF_MAP =
            new TypeReference<Map<String, String>>() {};

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

}
