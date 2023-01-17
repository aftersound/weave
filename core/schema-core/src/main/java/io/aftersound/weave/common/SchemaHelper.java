package io.aftersound.weave.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

public class SchemaHelper {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String BASE64 = "BASE64:";
    private static final String JSON = "JSON:";

    public static Schema parse(String sd) throws Exception {
        if (sd == null || sd.isEmpty()) {
            return null;
        }

        if (sd.startsWith(BASE64)) {
            byte[] content = Base64.getDecoder().decode(sd.substring(BASE64.length()));
            return MAPPER.readValue(content, Schema.class);
        }

        if (sd.startsWith(JSON)) {
            String content = sd.substring(JSON.length());
            return MAPPER.readValue(content, Schema.class);
        }

        return ResourceRegistry.get(sd);

    }

}
