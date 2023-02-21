package io.aftersound.weave.service.management;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.jackson.ObjectMapperBuilder;

import java.util.Map;

class Helper {

    public static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private static final TypeReference<Map<String, Object>> MAP_TYPE_REFERENCE = new TypeReference<Map<String, Object>>() {};

    private static final String INSERT_TRACE = "{\"op\":\"insert\",\"by\":\"USER\"}";
    private static final String UPDATE_TRACE = "{\"op\":\"update\",\"by\":\"USER\"}";
    private static final String DELETE_TRACE = "{\"op\":\"delete\",\"by\":\"USER\"}";

    public static String toJson(Map<String, Object> m) {
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

    public static Map<String, Object> parseAsMap(String json) {
        try {
            return Helper.MAPPER.readValue(json, MAP_TYPE_REFERENCE);
        } catch (Exception e) {
            return null;
        }
    }

    public static String insertedByTrace(String user) {
        return INSERT_TRACE.replace("USER", user);
    }

    public static String updatedByTrace(String user) {
        return UPDATE_TRACE.replace("USER", user);
    }

    public static String deletedByTrace(String user) {
        return DELETE_TRACE.replace("USER", user);
    }
}
