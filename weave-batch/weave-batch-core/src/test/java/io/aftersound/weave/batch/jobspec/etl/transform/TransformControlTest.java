package io.aftersound.weave.batch.jobspec.etl.transform;

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

public class TransformControlTest {

    @Test
    public void testGetType() {
        TransformControl ec = new TransformControl() {

            @Override
            public String getType() {
                return "TEST";
            }
        };
        assertEquals("TEST", ec.getType());
    }

    @Test
    public void testJson() throws IOException {
        List<NamedType<TransformControl>> namedTypes = new ArrayList<>();
        namedTypes.add(TestTransformControl1.TYPE);
        ObjectMapper om = ObjectMapperBuilder.forJson().with(
                new BaseTypeDeserializer<>(
                        TransformControl.class,
                        "type",
                        namedTypes
                )
        ).build();

        TestTransformControl1 ttc1 = new TestTransformControl1();
        String json = om.writeValueAsString(ttc1);

        TransformControl tc = om.readValue(json, TransformControl.class);
        assertEquals(TestTransformControl1.TYPE.name(), tc.getType());
        assertTrue(tc instanceof TestTransformControl1);
    }

    @Test(expected = InvalidDefinitionException.class)
    public void testJsonFailed() throws IOException {
        ObjectMapper om = ObjectMapperBuilder.forJson().build();
        TestTransformControl1 ttc1 = new TestTransformControl1();
        String json = om.writeValueAsString(ttc1);
        TransformControl dsControl = om.readValue(json, TransformControl.class);
        assertEquals(TestTransformControl1.TYPE.name(), dsControl.getType());
    }
    
}
