package io.aftersound.service;

import org.glassfish.jersey.uri.PathTemplate;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathTemplateTest {

    @Test
    public void test1() {
        PathTemplate template = new PathTemplate("/user/{id}/address/{address}");

        Map<String, String> variables = new HashMap<>();
        boolean match = template.match("/user/aftersound/address/888 Parkway", variables);
        assertTrue(match);
        assertEquals("aftersound", variables.get("id"));
        assertEquals("888 Parkway", variables.get("address"));
    }

}
