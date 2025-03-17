package io.aftersound.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.junit.Assert.*;

public class ObjectMapperBuilderTest {

    @Test
    public void forJson() {
        ObjectMapper objectMapper = ObjectMapperBuilder.forJson().build();
        assertNotNull(objectMapper);
    }

    @Test
    public void forYaml() {
        ObjectMapper objectMapper = ObjectMapperBuilder.forYAML().build();
        assertNotNull(objectMapper);
    }

}