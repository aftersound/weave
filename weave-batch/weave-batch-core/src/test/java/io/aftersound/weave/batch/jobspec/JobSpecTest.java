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

public class JobSpecTest {

    private static class TestJobSpec extends JobSpec {

        public static final NamedType<JobSpec> TYPE = NamedType.of("TEST", TestJobSpec.class);

        @Override
        public String getType() {
            return TYPE.name();
        }

        @Override
        public JobSpec copy() {
            TestJobSpec c = new TestJobSpec();
            c.setId(this.getId());
            return c;
        }
    }

    @Test
    public void testSetterGetter() {
        JobSpec js = new TestJobSpec();

        js.setId("test");
        assertEquals("test", js.getId());

        DataSourceControl dsc = new TestDataSourceControl1();
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

        JobSpec js = new TestJobSpec();
        js.setId("test");

        String json = om.writeValueAsString(js);

        JobSpec restored = om.readValue(json, JobSpec.class);
        assertTrue(restored instanceof TestJobSpec);
        assertEquals("test", restored.getId());
    }

    @Test
    public void testRelationship() {
        assertTrue(JobSpec.class.isAssignableFrom(TestJobSpec.class));
    }

}
