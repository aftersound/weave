package io.aftersound.weave.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ViewTest {

    @Test
    public void testView() {
        Map<String, Object> rawRecord = new LinkedHashMap<>();
        rawRecord.put("firstName", "Tom");
        rawRecord.put("lastName", "Cat");
        rawRecord.put("age", Integer.valueOf(80));
        rawRecord.put("sex", "Male");

        List<Field> fields = Arrays.asList(
                Field.of("name", "MVEL2:@{firstName} @{lastName}"),
                Field.of("age", "age"),
                Field.of("gender", "sex")
        );

        Schema schema = new Schema();
        schema.setName("Customized");
        schema.setFields(fields);

        View view = new View();
        view.setName("Simple");
        view.setSchema(schema);

        Map<String, String> viewConfig = new LinkedHashMap<>();
        viewConfig.put("type", "FlatMap");
        viewConfig.put("include.summary", "true");
        viewConfig.put("summary.name", "summary");
        view.setConfig(viewConfig);
    }

}