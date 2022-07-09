package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.actor.ActorBindingsConfig;

import java.util.*;

public class CompositeActorBindingsConfigProvider extends ConfigProvider<ActorBindingsConfig> {

    private final List<ConfigProvider<ActorBindingsConfig>> configProviders;

    CompositeActorBindingsConfigProvider(ConfigProvider<ActorBindingsConfig>... configProviders) {
        if (configProviders != null) {
            this.configProviders = Arrays.asList(configProviders);
        } else {
            this.configProviders = Collections.emptyList();
        }
    }

    @Override
    protected void setConfigReader(ObjectMapper configReader) {
        configProviders.forEach(cp -> cp.setConfigReader(configReader));
    }

    @Override
    protected List<ActorBindingsConfig> getConfigList() {
        Map<String, ActorBindingsConfig> abcByGroup = new LinkedHashMap<>();
        for (ConfigProvider<ActorBindingsConfig> cp : configProviders) {
            List<ActorBindingsConfig> cl = cp.getConfigList();
            cl.forEach(
                    c -> {
                        ActorBindingsConfig abc = abcByGroup.get(c.getGroup());
                        if (abc == null) {
                            abc = new ActorBindingsConfig();
                            abc.setGroup(c.getGroup());
                            abc.setBaseType(c.getBaseType());
                            abc.setTypes(new ArrayList<>());
                            abcByGroup.put(c.getGroup(), abc);
                        }
                        abc.getTypes().addAll(c.getTypes());
                    }
            );
        }
        return new ArrayList<>(abcByGroup.values());
    }

}
