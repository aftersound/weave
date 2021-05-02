package io.aftersound.weave.service.runtime;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.component.ComponentRepository;

import java.util.Collection;

class ComponentRepositoryImpl implements ComponentRepository {

    private final ComponentRegistry componentRegistry;

    ComponentRepositoryImpl(ComponentRegistry componentRegistry) {
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
