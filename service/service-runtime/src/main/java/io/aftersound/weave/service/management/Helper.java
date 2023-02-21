package io.aftersound.weave.service.management;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.jackson.ObjectMapperBuilder;

import java.util.Map;

class Helper {
    public static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private static final String INSERT_TRACE = "{\"op\":\"insert\",\"by\":\"USER\"}";
    private static final String UPDATE_TRACE = "{\"op\":\"update\",\"by\":\"USER\"}";
    private static final String DELETE_TRACE = "{\"op\":\"delete\",\"by\":\"USER\"}";

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
            return Helper.MAPPER.readValue(json, new TypeReference<Map<String, String>>() {});
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
