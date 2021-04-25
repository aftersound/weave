package io.aftersound.weave.component;

import io.aftersound.weave.common.Key;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class ComponentConfigKeysRegistry {

    public static final ComponentConfigKeysRegistry INSTANCE = new ComponentConfigKeysRegistry();

    private static final Collection<Key<?>> EMPTY_KEYS = Collections.emptyList();

    private final ConcurrentHashMap<String, Collection<Key<?>>> configKeysByComponentType = new ConcurrentHashMap<>();

    private ComponentConfigKeysRegistry() {
    }

    public void registerConfigKeys(String componentType, Collection<Key<?>> configKeys) {
        configKeysByComponentType.put(componentType, configKeys);
    }

    public Collection<Key<?>> getConfigKeys(String componentType) {
        if (componentType != null) {
            Collection<Key<?>> configKeys = configKeysByComponentType.get(componentType);
            return configKeys != null ? configKeys : EMPTY_KEYS;
        } else {
            return EMPTY_KEYS;
        }
    }
}
