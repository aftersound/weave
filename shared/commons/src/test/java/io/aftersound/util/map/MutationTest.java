package io.aftersound.util.map;


import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class MutationTest {

//    @Test
    public void mutate() {
        final Map<String, Object> map = Builder.linkedHashMap()
                .put(
                        "firstName", "Nikola",
                        "lastName", "Tesla",
                        "title", "Inventor",
                        "inventions", new ArrayList<Map<String, Object>>(
                                Arrays.<Map<String, Object>>asList(
                                        Builder.linkedHashMap().put("name", "Tesla Coil", "time", "05/24/1875").buildModifiable(),
                                        Builder.linkedHashMap().put("name", "AC", "time", "06/26/1886").buildModifiable()
                                )
                        )
                )
                .buildModifiable();

        assertEquals("Nikola", Path.of("firstName").update("Nikola1").on(map));
        assertEquals("Nikola1", Path.of("firstName").query().on(map));
        assertEquals("Nikola1", Path.of("firstName").update("Nikola").on(map));

        assertEquals("05/24/1875", Path.of("inventions[(name=='Tesla Coil')|0].time").update("05/25/1875").on(map));
        assertEquals("05/25/1875", Path.of("inventions[(name=='Tesla Coil')|0].time").query().on(map));
        assertEquals("05/25/1875", Path.of("inventions[(name=='Tesla Coil')|0].time").update("05/24/1875").on(map));
    }

}