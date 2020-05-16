package io.aftersound.weave.batch.jobspec.fetl;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.jobspec.Utils;
import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.batch.jobspec.datasource.TestDataSourceControl1;
import io.aftersound.weave.batch.jobspec.datasource.TestDataSourceControl2;
import io.aftersound.weave.batch.jobspec.etl.extract.ExtractControl;
import io.aftersound.weave.batch.jobspec.etl.extract.TestExtractControl1;
import io.aftersound.weave.batch.jobspec.fetl.file.TestFileHandlingControl;
import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.batch.jobspec.etl.load.TestLoadControl1;
import io.aftersound.weave.batch.jobspec.etl.transform.TestTransformControl1;
import io.aftersound.weave.batch.jobspec.etl.transform.TransformControl;
import io.aftersound.weave.filehandler.FileHandlingControl;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class FETLJobSpecTest {

    @Test
    public void testSetterGetter() {
        FETLJobSpec js = new FETLJobSpec();

        js.setId("test");
        assertEquals("test", js.getId());

        DataSourceControl dsc = new TestDataSourceControl1();
        js.setDataSourceControls(Arrays.asList(dsc));
        assertSame(dsc, js.getDataSourceControls().get(0));

        FileHandlingControl fhc = new TestFileHandlingControl();
        js.setSourceFileHandlingControl(fhc);
        assertSame(fhc, js.getSourceFileHandlingControl());

        ExtractControl ec = new TestExtractControl1();
        js.setExtractControl(ec);
        assertSame(ec, js.getExtractControl());

        TransformControl tc = new TestTransformControl1();
        js.setTransformControl(tc);
        assertSame(tc, js.getTransformControl());

        LoadControl lc = new TestLoadControl1();
        js.setLoadControl(lc);
        assertSame(lc, js.getLoadControl());
    }

    @Test
    public void testJson() throws IOException {
        ObjectMapper om = ObjectMapperBuilder.forJson()
                .with(new BaseTypeDeserializer<JobSpec>(
                        JobSpec.class,
                        "type",
                        Arrays.asList(
                                FETLJobSpec.TYPE
                        )))
                .with(new BaseTypeDeserializer<DataSourceControl>(
                        DataSourceControl.class,
                        "type",
                        Arrays.asList(
                                TestDataSourceControl1.TYPE
                        )))
                .with(new BaseTypeDeserializer<FileHandlingControl>(
                        FileHandlingControl.class,
                        "type",
                        Arrays.asList(
                                TestFileHandlingControl.TYPE
                        )))
                .with(new BaseTypeDeserializer<ExtractControl>(
                        ExtractControl.class,
                        "type",
                        Arrays.asList(
                                TestExtractControl1.TYPE
                        )))
                .with(new BaseTypeDeserializer<TransformControl>(
                        TransformControl.class,
                        "type",
                        Arrays.asList(
                                TestTransformControl1.TYPE
                        )))
                .with(new BaseTypeDeserializer<LoadControl>(
                        LoadControl.class,
                        "type",
                        Arrays.asList(
                                TestLoadControl1.TYPE
                        )))
                .build();

        FETLJobSpec js = new FETLJobSpec();
        js.setId("fetl-123");
        DataSourceControl dsc = new TestDataSourceControl1();
        js.setDataSourceControls(Arrays.asList(dsc));
        js.setSourceFileHandlingControl(new TestFileHandlingControl());
        js.setExtractControl(new TestExtractControl1());
        js.setTransformControl(new TestTransformControl1());
        js.setLoadControl(new TestLoadControl1());

        String json = om.writeValueAsString(js);

        JobSpec js1 = om.readValue(json, JobSpec.class);
        assertTrue(js1 instanceof FETLJobSpec);
        FETLJobSpec restored = (FETLJobSpec)js1;
        assertTrue(restored.getDataSourceControls().get(0) instanceof TestDataSourceControl1);
        assertTrue(restored.getSourceFileHandlingControl() instanceof TestFileHandlingControl);
        assertTrue(restored.getExtractControl() instanceof TestExtractControl1);
        assertTrue(restored.getTransformControl() instanceof TestTransformControl1);
        assertTrue(restored.getLoadControl() instanceof TestLoadControl1);
    }

    @Test(expected = JsonMappingException.class)
    public void testJsonFailure() throws IOException {
        ObjectMapper om = Utils.fetlJobSpecJsonReader(
                TestDataSourceControl1.TYPE, TestExtractControl1.TYPE, TestTransformControl1.TYPE, TestLoadControl1.TYPE);

        FETLJobSpec js = new FETLJobSpec();
        DataSourceControl dsc = new TestDataSourceControl2();
        js.setDataSourceControls(Arrays.asList(dsc));
        js.setSourceFileHandlingControl(new TestFileHandlingControl());
        js.setExtractControl(new TestExtractControl1());
        js.setTransformControl(new TestTransformControl1());
        js.setLoadControl(new TestLoadControl1());

        String json = om.writeValueAsString(js);

        FETLJobSpec restored = om.readValue(json, FETLJobSpec.class);
        assertTrue(restored.getDataSourceControls().get(0) instanceof TestDataSourceControl1);
        assertTrue(restored.getSourceFileHandlingControl() instanceof TestFileHandlingControl);
        assertTrue(restored.getExtractControl() instanceof TestExtractControl1);
        assertTrue(restored.getTransformControl() instanceof TestTransformControl1);
        assertTrue(restored.getLoadControl() instanceof TestLoadControl1);
    }

    @Test
    public void testHelperMethods() {
        FETLJobSpec js = new FETLJobSpec();
        DataSourceControl dsc = new TestDataSourceControl1();
        js.setDataSourceControls(Arrays.asList(dsc));
        js.setSourceFileHandlingControl(new TestFileHandlingControl());
        js.setExtractControl(new TestExtractControl1());
        js.setTransformControl(new TestTransformControl1());
        js.setLoadControl(new TestLoadControl1());

        TestDataSourceControl1 tdsc1 = (TestDataSourceControl1)js.getDataSourceControls().get(0);
        assertNotNull(tdsc1);

        TestExtractControl1 tec1 = js.extractControl();
        assertNotNull(tec1);

        TestTransformControl1 ttc1 = js.transformControl();
        assertNotNull(ttc1);

        TestLoadControl1 tlc1 = js.loadControl();
        assertNotNull(tlc1);

        try {
            TestDataSourceControl2 tdsc2 = (TestDataSourceControl2)js.getDataSourceControls().get(0);
            assertNotNull(tdsc2);
        } catch (Exception e) {
            assertTrue(e instanceof ClassCastException);
        }
    }

}