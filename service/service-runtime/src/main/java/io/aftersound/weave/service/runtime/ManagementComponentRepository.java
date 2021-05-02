package io.aftersound.weave.service.runtime;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceInstance;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.cache.CacheRegistry;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class ManagementComponentRepository implements ComponentRepository {

    private final Map<String, Object> componentById;

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
        componentById.put(Constants.ADMIN_COMPONENT_REPOSITORY, adminOnlyComponentRepository);
        componentById.put(ComponentRepository.class.getName(), componentRepository);
        componentById.put(CacheRegistry.class.getName(), cacheRegistry);
        componentById.put(ComponentManager.class.getName(), componentManager);
        componentById.put(Constants.ADMIN_SERVICE_METADATA_REGISTRY, adminOnlyServiceMetadataRegistry);
        componentById.put(ServiceMetadataRegistry.class.getName(), serviceMetadataRegistry);

        this.componentById = componentById;
    }

    @Override
    public <COMPONENT> COMPONENT getComponent(String id) {
        return (COMPONENT) componentById.get(id);
    }

    @Override
    public <COMPONENT> COMPONENT getComponent(String id, Class<COMPONENT> type) {
        Object c = componentById.get(id);
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
        return componentById.keySet();
    }
}
