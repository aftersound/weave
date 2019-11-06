package io.aftersound.weave.batch.jobspec.ft;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.batch.jobspec.datasource.DataSourceControls;
import io.aftersound.weave.batch.jobspec.datasource.TestDataSourceControl1;
import io.aftersound.weave.batch.jobspec.ft.file.SourceFileHandlingControl;
import io.aftersound.weave.batch.jobspec.ft.file.TargetFileHandlingControl;
import io.aftersound.weave.filehandler.FileHandlingControl;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class FTJobSpecTest {

    @Test
    public void testSetterGetter() {
        FTJobSpec js = new FTJobSpec();

        js.setDataSourceControls(DataSourceControls.from(new TestDataSourceControl1()).all());
        js.setSourceFileHandlingControl(new SourceFileHandlingControl());
        js.setTargetFileHandlingControl(new TargetFileHandlingControl());

        assertNotNull(js.getDataSourceControls());
        assertEquals(1, js.getDataSourceControls().size());
        assertNotNull(js.getSourceFileHandlingControl());
        assertNotNull(js.getTargetFileHandlingControl());
    }

    @Test
    public void testJson() throws Exception {
        ObjectMapper om = ObjectMapperBuilder.forJson()
                .with(new BaseTypeDeserializer<JobSpec>(
                        JobSpec.class,
                        "type",
                        Arrays.asList(
                                FTJobSpec.TYPE
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
                                SourceFileHandlingControl.TYPE,
                                TargetFileHandlingControl.TYPE
                        )))
                .build();

        FTJobSpec js = new FTJobSpec();

        js.setDataSourceControls(DataSourceControls.from(new TestDataSourceControl1()).all());
        js.setSourceFileHandlingControl(new SourceFileHandlingControl());
        js.setTargetFileHandlingControl(new TargetFileHandlingControl());

        String json = om.writeValueAsString(js);

        JobSpec js1 = om.readValue(json, JobSpec.class);
        assertTrue(js1 instanceof FTJobSpec);
        FTJobSpec restored = (FTJobSpec)js1;
        assertTrue(restored.getDataSourceControls().get(0) instanceof TestDataSourceControl1);
        assertTrue(restored.getSourceFileHandlingControl() instanceof SourceFileHandlingControl);
        assertTrue(restored.getTargetFileHandlingControl() instanceof TargetFileHandlingControl);
    }

}