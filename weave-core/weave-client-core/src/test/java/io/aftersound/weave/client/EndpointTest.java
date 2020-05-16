package io.aftersound.weave.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class EndpointTest {

    @Test
    public void testJson() throws Exception {
        Endpoint endpoint = new Endpoint();
        endpoint.setType("TEST");
        endpoint.setId("id");
        Map<String, String> options = MapBuilder.hashMap().kv("host", "localhost").build();
        endpoint.setOptions(options);

        ObjectMapper om = ObjectMapperBuilder.forJson().build();
        String json = om.writeValueAsString(endpoint);

        Endpoint restored = om.readValue(json, Endpoint.class);
        assertEquals(endpoint.getType(), restored.getType());
        assertEquals(endpoint.getId(), restored.getId());
        assertEquals(endpoint.getOptions().get("host"), restored.getOptions().get("host"));
    }

}