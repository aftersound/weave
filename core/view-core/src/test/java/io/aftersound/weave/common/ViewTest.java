package io.aftersound.weave.common;

import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class ViewTest {

    @Test
    public void testView() {
        Map<String, Object> rawRecord = new LinkedHashMap<>();
        rawRecord.put("firstName", "Tom");
        rawRecord.put("lastName", "Cat");
        rawRecord.put("age", Integer.valueOf(80));
        rawRecord.put("sex", "Male");

        List<Field> fields = Arrays.asList(
                Field.of("name", Arrays.asList("firstName", "lastName")),
                Field.of("age", "age"),
                Field.of("gender", "sex")
        );

        Schema schema = new Schema();
        schema.setName("Customized");
        schema.setFields(fields);

        ViewConfig viewConfig = new ViewConfig();
        viewConfig.setType("Simple");
        viewConfig.setSchema(schema);

        Element elementRecords = new Element();
        elementRecords.setName("records");
        Map<String, String> elementRecordsConfig = new LinkedHashMap<>();
        elementRecordsConfig.put("name", "items");
        elementRecordsConfig.put("default.output.fields", "name,age,gender");
        elementRecords.setConfig(elementRecordsConfig);

        Element elementSummary = new Element();
        elementSummary.setName("summary");

        viewConfig.setElements(Arrays.asList(elementRecords, elementSummary));

    }

}