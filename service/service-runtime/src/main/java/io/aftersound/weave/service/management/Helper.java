package io.aftersound.weave.service.management;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.jackson.ObjectMapperBuilder;

class Helper {
    public static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();
}
