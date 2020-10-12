package io.aftersound.weave.component;

import io.aftersound.weave.common.Key;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentConfigKeysRegistry {

    public static final ComponentConfigKeysRegistry INSTANCE = new ComponentConfigKeysRegistry();

    private final ConcurrentHashMap<String, Collection<Key<?>>> configKeysByComponentType = new ConcurrentHashMap<>();

    private ComponentConfigKeysRegistry() {
    }

    public void registerConfigKeys(String componentType, Collection<Key<?>> configKeys) {
        configKeysByComponentType.put(componentType, configKeys);
    }

    public Collection<Key<?>> getConfigKeys(String componentType) {
        return configKeysByComponentType.get(componentType);
    }
}
