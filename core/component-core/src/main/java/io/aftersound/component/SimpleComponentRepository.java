package io.aftersound.component;

import io.aftersound.common.NamedType;

import java.util.Collection;

class SimpleComponentRepository implements ComponentRepository {

    private final ComponentRegistry componentRegistry;

    SimpleComponentRepository(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }

    @Override
    public <COMPONENT> COMPONENT getComponent(String id) {
        return componentRegistry.getComponent(id);
    }

    @Override
    public <COMPONENT> COMPONENT getComponent(String id, Class<COMPONENT> type) {
        Object c = componentRegistry.getComponent(id);
        if (type.isInstance(c)) {
            return type.cast(c);
        } else {
            return null;
        }
    }

    @Override
    public <C> C getComponent(NamedType<C> namedType) {
        return getComponent(namedType.name(), namedType.type());
    }

    @Override
    public Collection<String> componentIds() {
        return componentRegistry.getComponentIds();
    }

}
