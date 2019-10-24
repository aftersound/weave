package io.aftersound.weave.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class AppConfigTest {

    @Test
    public void jsonSerDeser() throws Exception {
        AppConfig config = new AppConfig();
        config.setJsonSpecTypes(Arrays.asList(
                "io.aftersound.weave.batch.jobspec.fetl.FETLJobSpec"
        ));

        config.setDataSourceControlTypes(Arrays.asList(
                "io.aftersound.weave.hdfs.HdfsDataSourceControl"
        ));

        config.setExtractControlTypes(Arrays.asList(

        ));


        config.setTransformControlTypes(Arrays.asList(

        ));

        config.setLoadControlTypes(Arrays.asList(

        ));

        String json = new ObjectMapper().writeValueAsString(config);

        assertNotNull(json);
    }

}