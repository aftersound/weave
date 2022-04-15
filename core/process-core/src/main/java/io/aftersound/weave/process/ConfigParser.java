package io.aftersound.weave.process;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ConfigParser {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Parse config in raw format to specific type
     *
     * @param rawConfig
     *          config in raw format, typically in Map
     * @param type
     *          expected type of config
     * @param <T>
     *          generic type of config
     * @return a config object in expected type
     */
    public static <T> T parse(Object rawConfig, Class<T> type) {
        try {
            return MAPPER.treeToValue(MAPPER.valueToTree(rawConfig), type);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred on parse config", e);
        }
    }

}
