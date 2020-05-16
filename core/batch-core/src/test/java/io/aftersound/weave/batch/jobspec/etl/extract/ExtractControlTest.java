package io.aftersound.weave.batch.jobspec.etl.extract;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.common.NamedType;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExtractControlTest {

    @Test
    public void testGetType() {
        ExtractControl ec = new ExtractControl() {

            @Override
            public String getType() {
                return "TEST";
            }
        };
        assertEquals("TEST", ec.getType());
    }

    @Test
    public void testJson() throws IOException {
        List<NamedType<ExtractControl>> namedTypes = new ArrayList<>();
        namedTypes.add(TestExtractControl1.TYPE);
        ObjectMapper om = ObjectMapperBuilder.forJson().with(
                new BaseTypeDeserializer<>(
                        ExtractControl.class,
                        "type",
                        namedTypes
                )
        ).build();

        TestExtractControl1 tec1 = new TestExtractControl1();
        String json = om.writeValueAsString(tec1);

        ExtractControl ec = om.readValue(json, ExtractControl.class);
        assertEquals(TestExtractControl1.TYPE.name(), ec.getType());
        assertTrue(ec instanceof  TestExtractControl1);
    }

    @Test(expected = InvalidDefinitionException.class)
    public void testJsonFailed() throws IOException {
        ObjectMapper om = ObjectMapperBuilder.forJson().build();
        TestExtractControl1 tec1 = new TestExtractControl1();
        String json = om.writeValueAsString(tec1);
        ExtractControl ec = om.readValue(json, ExtractControl.class);
        assertEquals(TestExtractControl1.TYPE.name(), ec.getType());
    }

}
