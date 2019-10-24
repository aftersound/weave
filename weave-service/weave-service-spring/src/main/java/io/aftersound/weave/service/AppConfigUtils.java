package io.aftersound.weave.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.common.NamedTypes;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;

import java.io.File;
import java.util.List;

class AppConfigUtils {

    static <CONTROL, ACTOR, PRODUCT> ActorBindings<CONTROL, ACTOR, PRODUCT> loadAndInitActorBindings(
            String typesJsonPath,
            Class<CONTROL> controlClass,
            Class<PRODUCT> productClass,
            boolean tolerateIndividualException) throws Exception {

        List<String> types = loadTypes(typesJsonPath);

        return ActorBindingsUtil.loadActorBindings(
                types,
                controlClass,
                productClass,
                tolerateIndividualException);
    }

    static ObjectMapper createServiceMetadataReader(
            NamedTypes<ExecutionControl> executionControlTypes,
            NamedTypes<CacheControl> cacheControlTypes,
            NamedTypes<DeriveControl> deriveControlTypes) {

        BaseTypeDeserializer<ExecutionControl> executionControlTypeDeserializer = new BaseTypeDeserializer<>(
                ExecutionControl.class,
                "type",
                executionControlTypes.all()
        );

        BaseTypeDeserializer<CacheControl> cacheControlBaseTypeDeserializer = new BaseTypeDeserializer<>(
                CacheControl.class,
                "type",
                cacheControlTypes.all()
        );

        BaseTypeDeserializer<DeriveControl> deriveControlBaseTypeDeserializer = new BaseTypeDeserializer<>(
                DeriveControl.class,
                "type",
                deriveControlTypes.all()
        );

        return ObjectMapperBuilder.forJson()
                .with(executionControlTypeDeserializer)
                .with(cacheControlBaseTypeDeserializer)
                .with(deriveControlBaseTypeDeserializer)
                .build();
    }

    private static List<String> loadTypes(String typesJsonPath) throws Exception {
        File typesJsonFile = PathHandle.of(typesJsonPath).path().toFile();

        ObjectMapper mapper = ObjectMapperBuilder.forJson().build();

        return mapper.readValue(
                typesJsonFile,
                new TypeReference<List<String>>() {}
        );
    }

}
