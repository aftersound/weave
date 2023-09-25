package io.aftersound.weave.common;

import io.aftersound.weave.utils.MapBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParseAsMapFuncTest {

    @BeforeClass
    public static void setup() throws Exception {
        MasterValueFuncFactory.init(TestValueFuncFactory.class.getName());
    }

    @Test
    public void parseRecords() {
        List<Field> fieldList = Arrays.asList(
                Field.stringFieldBuilder("firstName").withFunc("MAP:GET(first_name)").build(),
                Field.stringFieldBuilder("lastName").withFunc("MAP:GET(last_name)").build(),
                Field.stringFieldBuilder("description").withFunc("MAP:TO_STRING(%s %s is a great inventor,firstName,lastName)[ON:TARGET]").build()
        );

        Map<String, Object> record = new ParseAsMapFunc<Map<String, Object>>(Fields.from(fieldList)).apply(
                MapBuilder.linkedHashMap()
                        .keys("first_name", "last_name")
                        .values("Nikola", "Tesla")
                        .build()
        );
        assertNotNull(record);
        assertEquals("Nikola", record.get("firstName"));
        assertEquals("Tesla", record.get("lastName"));
        assertEquals("Nikola Tesla is a great inventor", record.get("description"));
    }

}
