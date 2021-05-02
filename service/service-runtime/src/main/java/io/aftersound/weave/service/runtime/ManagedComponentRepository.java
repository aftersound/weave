package io.aftersound.weave.service.runtime;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.dependency.Declaration;

import java.util.Collection;

class ManagedComponentRepository implements ComponentRepository {

    private final Declaration declaration;
    private final ComponentRepository delegate;

    ManagedComponentRepository(Declaration declaration, ComponentRepository componentRepository) {
        this.declaration = declaration;
        this.delegate = componentRepository;
    }

    @Override
    public <COMPONENT> COMPONENT getComponent(String id) {
        if (declaration.isRequired(id)) {
            return delegate.getComponent(id);
        }
        return null;
    }

    @Override
    public <COMPONENT> COMPONENT getComponent(String id, Class<COMPONENT> type) {
        if (declaration.isRequired(id)) {
            return delegate.getComponent(id, type);
        }
        return null;
    }

    @Override
    public <C> C getComponent(NamedType<C> namedType) {
        if (declaration.isRequired(namedType.name())) {
            return delegate.getComponent(namedType);
        }
        return null;
    }

    @Override
    public Collection<String> componentIds() {
        return declaration.requiredIds();
    }
}
