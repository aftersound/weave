package io.aftersound.weave.service.runtime;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ManagedComponents;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class ManagedComponentsImpl implements ManagedComponents {

    private Map<String, Object> componentByName = new HashMap<>();

    @Override
    public void setComponent(String name, Object component) {
        componentByName.put(name, component);
    }

    @Override
    public <C> void setComponent(NamedType<C> type, C component) {
        setComponent(type.name(), component);
    }

    @Override
    public <C> C getComponent(NamedType<C> componentType) {
        return getComponent(componentType.name(), componentType.type());
    }

    @Override
    public <C> C getComponent(String name, Class<C> componentType) {
        Object component = componentByName.get(name);
        if (componentType.isInstance(component)) {
            return componentType.cast(component);
        }
        return null;
    }

    @Override
    public Collection<String> componentNames() {
        return componentByName.keySet();
    }

}
