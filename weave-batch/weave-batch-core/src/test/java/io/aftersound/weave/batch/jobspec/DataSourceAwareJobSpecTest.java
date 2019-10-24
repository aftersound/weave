package io.aftersound.weave.batch.jobspec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.batch.jobspec.datasource.TestDataSourceControl1;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class DataSourceAwareJobSpecTest {

    private static class TestJobSpec extends DataSourceAwareJobSpec {

        public static final NamedType<JobSpec> TYPE = NamedType.of("TEST", TestJobSpec.class);

        @Override
        public String getType() {
            return TYPE.name();
        }

        @Override
        public JobSpec copy() {
            TestJobSpec c = new TestJobSpec();
            c.setId(this.getId());
            c.setDataSourceControls(this.getDataSourceControls());
            return c;
        }
    }

    @Test
    public void testSetterGetter() {
        DataSourceAwareJobSpec js = new TestJobSpec();

        js.setId("test");
        assertEquals("test", js.getId());

        DataSourceControl dsc = new TestDataSourceControl1();
        js.setDataSourceControls(Arrays.asList(dsc));
        assertEquals(1, js.getDataSourceControls().size());
    }

    @Test
    public void testJson() throws IOException {
        ObjectMapper om = ObjectMapperBuilder.forJson()
                .with(new BaseTypeDeserializer<JobSpec>(
                        JobSpec.class,
                        "type",
                        Arrays.asList(
                                TestJobSpec.TYPE
                        )))
                .with(new BaseTypeDeserializer<DataSourceControl>(
                        DataSourceControl.class,
                        "type",
                        Arrays.asList(
                                TestDataSourceControl1.TYPE
                        )))
                .build();

        DataSourceAwareJobSpec js = new TestJobSpec();
        js.setId("test");
        DataSourceControl dsc = new TestDataSourceControl1();
        js.setDataSourceControls(Arrays.asList(dsc));

        String json = om.writeValueAsString(js);

        JobSpec js1 = om.readValue(json, JobSpec.class);
        assertTrue(js1 instanceof TestJobSpec);
        DataSourceAwareJobSpec restored = (DataSourceAwareJobSpec)js1;
        assertEquals("test", restored.getId());
        assertEquals(1, restored.getDataSourceControls().size());
        assertTrue(restored.getDataSourceControls().get(0) instanceof TestDataSourceControl1);

    }

}