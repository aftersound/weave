package io.aftersound.weave.service.runtime;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.cache.CacheRegistry;

import java.util.*;

class ManagementComponentRepository implements ComponentRepository {

    private final Map<String, Object> componentById;
    private final ComponentRepository adminOnlyComponentRepository;
    private final ComponentRepository componentRepository;

    ManagementComponentRepository(
            ServiceInstance serviceInstance,
            ComponentRepository adminOnlyComponentRepository,
            ComponentRepository componentRepository,
            CacheRegistry cacheRegistry,
            ComponentManager componentManager,
            ServiceMetadataRegistry adminOnlyServiceMetadataRegistry,
            ServiceMetadataRegistry serviceMetadataRegistry) {
        Map<String, Object> componentById = new HashMap<>();
        componentById.put(ServiceInstance.class.getName(), serviceInstance);
        componentById.put(CacheRegistry.class.getName(), cacheRegistry);
        componentById.put(ComponentManager.class.getName(), componentManager);
        componentById.put(Constants.ADMIN_SERVICE_METADATA_REGISTRY, adminOnlyServiceMetadataRegistry);
        componentById.put(ServiceMetadataRegistry.class.getName(), serviceMetadataRegistry);
        this.componentById = Collections.unmodifiableMap(componentById);

        this.adminOnlyComponentRepository = adminOnlyComponentRepository;
        this.componentRepository = componentRepository;
    }

    @Override
    public <COMPONENT> COMPONENT getComponent(String id) {
        Object c = componentById.get(id);
        if (c != null) {
            return (COMPONENT) c;
        }

        c = adminOnlyComponentRepository.getComponent(id);
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

        c = adminOnlyComponentRepository.getComponent(id);
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
        ids.addAll(adminOnlyComponentRepository.componentIds());
        ids.addAll(componentRepository.componentIds());
        return ids;
    }
}
