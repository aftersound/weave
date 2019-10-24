package io.aftersound.weave.batch.dsv;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.batch.jobspec.etl.extract.ExtractControl;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DSVExtractControlTest {

    @Test
    public void testJson() throws Exception {
        DSVExtractControl ec = new DSVExtractControl();
        ec.setArchiveFormat(ArchiveFormat.Plain);
        ec.setCharset("UTF-8");
        ec.setDelimiter(',');
        ec.setColumnNames(Arrays.asList("C1", "C2"));
        ec.setSkipHeader(true);
        ec.setEnrichedColumnNames(Arrays.asList("C3"));

        BaseTypeDeserializer<ExtractControl> ecDeserializer = new BaseTypeDeserializer<>(
                ExtractControl.class,
                "type",
                Arrays.asList(DSVExtractControl.TYPE)
        );

        ObjectMapper mapper = ObjectMapperBuilder.forJson().with(ecDeserializer).build();
        String json = mapper.writeValueAsString(ec);

        ExtractControl deserialised = mapper.readValue(json, ExtractControl.class);
        assertTrue(deserialised instanceof DSVExtractControl);
        DSVExtractControl restored = (DSVExtractControl)deserialised;
        assertEquals(ec.getArchiveFormat(), restored.getArchiveFormat());
        assertEquals(ec.getCharset(), restored.getCharset());
        assertEquals(ec.getDelimiter(), restored.getDelimiter());
        assertEquals(ec.getColumnNames(), restored.getColumnNames());
        assertEquals(ec.getEnrichedColumnNames(), restored.getEnrichedColumnNames());
        assertEquals(ec.format(), restored.format());
    }

}