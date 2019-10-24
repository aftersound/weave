package io.aftersound.weave.batch.jobspec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.batch.jobspec.etl.extract.ExtractControl;
import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.batch.jobspec.etl.transform.TransformControl;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.common.NamedType;

import java.util.Arrays;

public class Utils {

    public static ObjectMapper jobSpecJsonReader(NamedType<JobSpec>... jobSpecTypes) {
        BaseTypeDeserializer<JobSpec> jobSpecDeserializer = new BaseTypeDeserializer<>(
                JobSpec.class,
                "type",
                Arrays.asList(
                        jobSpecTypes
                )
        );
        return ObjectMapperBuilder.forJson()
                .with(jobSpecDeserializer)
                .build();
    }

    public static ObjectMapper fetlJobSpecJsonReader(NamedType<DataSourceControl> dscType, NamedType<ExtractControl> ecType, NamedType<TransformControl> tcType, NamedType<LoadControl> lcType) {
        BaseTypeDeserializer<DataSourceControl> dscDeserializer = new BaseTypeDeserializer<>(
                DataSourceControl.class,
                "type",
                Arrays.asList(
                        dscType
                )
        );
        BaseTypeDeserializer<ExtractControl> ecDeserializer = new BaseTypeDeserializer<>(
                ExtractControl.class,
                "type",
                Arrays.asList(
                        ecType
                )
        );
        BaseTypeDeserializer<TransformControl> tcDeserializer = new BaseTypeDeserializer<>(
                TransformControl.class,
                "type",
                Arrays.asList(
                        tcType
                )
        );
        BaseTypeDeserializer<LoadControl> lcDeserializer = new BaseTypeDeserializer<>(
                LoadControl.class,
                "type",
                Arrays.asList(
                        lcType
                )
        );
        return ObjectMapperBuilder.forJson()
                .with(dscDeserializer)
                .with(ecDeserializer)
                .with(tcDeserializer)
                .with(lcDeserializer)
                .build();
    }

}
