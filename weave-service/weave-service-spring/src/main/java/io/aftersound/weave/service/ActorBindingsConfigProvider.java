package io.aftersound.weave.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.file.PathHandle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ActorBindingsConfigProvider extends ConfigProvider<ActorBindingsConfig> {

    private final WeaveServiceProperties properties;

    public ActorBindingsConfigProvider(WeaveServiceProperties properties) {
        this.properties = properties;
    }

    @Override
    public List<ActorBindingsConfig> getConfigList() {
        Map<String, String> map = Stream.of(new String[][] {
                { "cache.factory.types", properties.getCacheFactoryTypesJson() },
                { "cache.key.generator.types", properties.getCacheKeyGeneratorTypesJson() },
                { "client.factory.types", properties.getClientFactoryTypesJson() },
                { "param.validator.types", properties.getParamValidatorTypesJson() },
                { "param.deriver.types", properties.getParamDeriverTypesJson() },
                { "data.format.types", properties.getDataFormatTypesJson() },
                { "authenticator.types", properties.getAuthenticatorTypesJson() },
                { "authorizor.types", properties.getAuthorizerTypesJson() },
                { "resource.manager.types", properties.getResourceManagerTypesJson() },
                { "service.executor.types", properties.getServiceExecutorTypesJson() },
                { "admin.resource.manager.types", properties.getAdminResourceManagerTypesJson() },
                { "admin.service.executor.types", properties.getAdminServiceExecutorTypesJson() }
        }).collect(Collectors.toMap(array -> array[0], array -> array[1]));

        List<ActorBindingsConfig> abcList = new ArrayList<>();
        for (Map.Entry<String, String> e : map.entrySet()) {
            List<String> extensionTypes = loadTypes(e.getValue(), configReader);
            abcList.add(createActorBindingsConfig(e.getKey(), extensionTypes));
        }
        return abcList;
    }

    private static List<String> loadTypes(String typesJsonPath, ObjectMapper mapper) {
        File typesJsonFile = PathHandle.of(typesJsonPath).path().toFile();
        try {
            return mapper.readValue(
                    typesJsonFile,
                    new TypeReference<List<String>>() {}
            );
        } catch (IOException e) {
            throw new RuntimeException("", e);
        }
    }

    private static ActorBindingsConfig createActorBindingsConfig(String scenario, List<String> extensionTypes) {
        ActorBindingsConfig abc = new ActorBindingsConfig();
        abc.setScenario(scenario);
        abc.setExtensionTypes(extensionTypes);
        return abc;
    }
}
