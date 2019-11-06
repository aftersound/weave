package io.aftersound.weave.batch.jobspec.datasource;

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

public class DataSourceControlTest {

    @Test
    public void testGetType() {
        DataSourceControl dsControl = new DataSourceControl() {

            @Override
            public String getType() {
                return "test";
            }
        };
        assertEquals("test", dsControl.getType());
    }

    @Test
    public void testJson() throws IOException {
        List<NamedType<DataSourceControl>> namedTypes = new ArrayList<>();
        namedTypes.add(TestDataSourceControl1.TYPE);
        ObjectMapper om = ObjectMapperBuilder.forJson().with(
                new BaseTypeDeserializer<>(
                        DataSourceControl.class,
                        "type",
                        namedTypes
                )
        ).build();

        TestDataSourceControl1 dsc1 = new TestDataSourceControl1();
        String json = om.writeValueAsString(dsc1);

        DataSourceControl dsc = om.readValue(json, DataSourceControl.class);
        assertEquals(TestDataSourceControl1.TYPE.name(), dsc.getType());
        assertTrue(dsc instanceof TestDataSourceControl1);
    }

    @Test(expected = InvalidDefinitionException.class)
    public void testJsonFailed() throws IOException {
        ObjectMapper om = ObjectMapperBuilder.forJson().build();
        TestDataSourceControl1 dsc1 = new TestDataSourceControl1();
        String json = om.writeValueAsString(dsc1);
        DataSourceControl dsc = om.readValue(json, DataSourceControl.class);
        assertEquals(TestDataSourceControl1.TYPE.name(), dsc.getType());
    }

}
