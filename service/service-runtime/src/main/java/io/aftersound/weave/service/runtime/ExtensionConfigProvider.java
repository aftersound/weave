package io.aftersound.weave.service.runtime;

import io.aftersound.weave.actor.ActorBindingsConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class ExtensionConfigProvider {

    protected final List<ActorBindingsConfig> configs() {
        Map<String, ActorBindingsConfig> abcByGroup = new LinkedHashMap<>();

        List<ActorBindingsConfig> cl = this.getConfigList();
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

        EmbeddedRuntimeConfig.getExtensionConfigList().forEach(
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
        return new ArrayList<>(abcByGroup.values());
    }

    protected abstract List<ActorBindingsConfig> getConfigList();

}
