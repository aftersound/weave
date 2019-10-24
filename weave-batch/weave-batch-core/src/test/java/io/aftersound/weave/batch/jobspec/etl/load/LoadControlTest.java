package io.aftersound.weave.batch.jobspec.etl.load;

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

public class LoadControlTest {

    @Test
    public void testGetType() {
        LoadControl ec = new LoadControl() {

            @Override
            public String getType() {
                return "TEST";
            }
        };
        assertEquals("TEST", ec.getType());
    }

    @Test
    public void testJson() throws IOException {
        List<NamedType<LoadControl>> namedTypes = new ArrayList<>();
        namedTypes.add(TestLoadControl1.TYPE);
        ObjectMapper om = ObjectMapperBuilder.forJson().with(
                new BaseTypeDeserializer<>(
                        LoadControl.class,
                        "type",
                        namedTypes
                )
        ).build();

        TestLoadControl1 tlc1 = new TestLoadControl1();
        String json = om.writeValueAsString(tlc1);

        LoadControl lc = om.readValue(json, LoadControl.class);
        assertEquals(TestLoadControl1.TYPE.name(), lc.getType());
        assertTrue(lc instanceof TestLoadControl1);
    }

    @Test(expected = InvalidDefinitionException.class)
    public void testJsonFailed() throws IOException {
        ObjectMapper om = ObjectMapperBuilder.forJson().build();
        TestLoadControl1 tlc1 = new TestLoadControl1();
        String json = om.writeValueAsString(tlc1);
        LoadControl lc = om.readValue(json, LoadControl.class);
        assertEquals(TestLoadControl1.TYPE.name(), lc.getType());
    }
    
}
