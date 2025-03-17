package io.aftersound.service.runtime;

import io.aftersound.common.NamedType;
import io.aftersound.component.ComponentRepository;
import io.aftersound.service.ServiceInstance;
import io.aftersound.service.ServiceMetadataRegistry;
import io.aftersound.service.cache.CacheRegistry;

import java.util.*;

class ManagedComponentRepository implements ComponentRepository {

    private final Map<String, Object> componentById;
    private final ComponentRepository bootstrapComponentRepository;
    private final ComponentRepository componentRepository;

    ManagedComponentRepository(
            ServiceInstance serviceInstance,
            ComponentRepository bootstrapComponentRepository,
            ComponentRepository componentRepository,
            CacheRegistry cacheRegistry,
            ComponentManager componentManager,
            ServiceMetadataRegistry serviceMetadataRegistry) {
        Map<String, Object> componentById = new HashMap<>();
        componentById.put(ServiceInstance.class.getSimpleName(), serviceInstance);
        componentById.put(CacheRegistry.class.getName(), cacheRegistry);
        componentById.put(ComponentManager.class.getName(), componentManager);
        componentById.put(ServiceMetadataRegistry.class.getSimpleName(), serviceMetadataRegistry);
        this.componentById = Collections.unmodifiableMap(componentById);

        this.bootstrapComponentRepository = bootstrapComponentRepository;
        this.componentRepository = componentRepository;
    }

    @Override
    public <COMPONENT> COMPONENT getComponent(String id) {
        Object c = componentById.get(id);
        if (c != null) {
            return (COMPONENT) c;
        }

        c = bootstrapComponentRepository.getComponent(id);
        if (c != null) {
            return (COMPONENT) c;
        }

        return componentRepository.getComponent(id);
    }

    @Override
    public <COMPONENT> COMPONENT getComponent(String id, Class<COMPONENT> type) {
        Object c = componentById.get(id);
        if (c != null) {
            if (type.isInstance(c)) {
                return type.cast(c);
            } else {
                return null;
            }
        }

        c = bootstrapComponentRepository.getComponent(id);
        if (c != null) {
            if (type.isInstance(c)) {
                return type.cast(c);
            } else {
                return null;
            }
        }

        c = componentRepository.getComponent(id);
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
        Set<String> ids = new LinkedHashSet<>();
        ids.addAll(componentById.keySet());
        ids.addAll(bootstrapComponentRepository.componentIds());
        ids.addAll(componentRepository.componentIds());
        return ids;
    }
}
