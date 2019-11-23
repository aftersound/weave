package io.aftersound.weave.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.cache.CacheControl;
import io.aftersound.weave.cache.KeyControl;
import io.aftersound.weave.common.NamedTypes;
import io.aftersound.weave.file.PathHandle;
import io.aftersound.weave.jackson.BaseTypeDeserializer;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.security.AuthenticationControl;
import io.aftersound.weave.security.AuthorizationControl;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.metadata.param.Validation;

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
            NamedTypes<KeyControl> keyControlTypes,
            NamedTypes<Validation> validationTypes,
            NamedTypes<DeriveControl> deriveControlTypes,
            NamedTypes<AuthenticationControl> authenticationControlTypes,
            NamedTypes<AuthorizationControl> authorizationControlTypes) {

        BaseTypeDeserializer<ExecutionControl> executionControlTypeDeserializer =
                new BaseTypeDeserializer<>(
                        ExecutionControl.class,
                        "type",
                        executionControlTypes.all()
                );

        BaseTypeDeserializer<CacheControl> cacheControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        CacheControl.class,
                        "type",
                        cacheControlTypes.all()
                );

        BaseTypeDeserializer<KeyControl> keyControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        KeyControl.class,
                        "type",
                        keyControlTypes.all()
                );

        BaseTypeDeserializer<Validation> validationBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        Validation.class,
                        "type",
                        validationTypes.all()
                );

        BaseTypeDeserializer<DeriveControl> deriveControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        DeriveControl.class,
                        "type",
                        deriveControlTypes.all()
                );

        BaseTypeDeserializer<AuthenticationControl> authenticationControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        AuthenticationControl.class,
                        "type",
                        authenticationControlTypes.all()
                );

        BaseTypeDeserializer<AuthorizationControl> authorizationControlBaseTypeDeserializer =
                new BaseTypeDeserializer<>(
                        AuthorizationControl.class,
                        "type",
                        authorizationControlTypes.all()
                );

        return ObjectMapperBuilder.forJson()
                .with(executionControlTypeDeserializer)
                .with(cacheControlBaseTypeDeserializer)
                .with(keyControlBaseTypeDeserializer)
                .with(validationBaseTypeDeserializer)
                .with(deriveControlBaseTypeDeserializer)
                .with(authenticationControlBaseTypeDeserializer)
                .with(authorizationControlBaseTypeDeserializer)
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
