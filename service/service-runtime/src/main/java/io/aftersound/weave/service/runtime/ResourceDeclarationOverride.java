package io.aftersound.weave.service.runtime;

import io.aftersound.weave.resource.ResourceDeclaration;
import io.aftersound.weave.resource.ResourceType;
import io.aftersound.weave.resource.SimpleResourceDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ResourceDeclarationOverride {

    public static class ResourceTypeConfig {
        private String name;
        private String type;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    private String serviceExecutor;
    private List<ResourceTypeConfig> dependingResourceTypes;

    public String getServiceExecutor() {
        return serviceExecutor;
    }

    public void setServiceExecutor(String serviceExecutor) {
        this.serviceExecutor = serviceExecutor;
    }

    public List<ResourceTypeConfig> getDependingResourceTypes() {
        return dependingResourceTypes;
    }

    public void setDependingResourceTypes(List<ResourceTypeConfig> dependingResourceTypes) {
        this.dependingResourceTypes = dependingResourceTypes;
    }

    private static ResourceType<?>[] createResourceTypes(List<ResourceTypeConfig> rtcList) throws Exception {
        if (rtcList == null) {
            return new ResourceType<?>[0];
        }
        List<ResourceType<?>> resourceTypes = new ArrayList<>(rtcList.size());
        for (ResourceTypeConfig rtc : rtcList) {
            resourceTypes.add(
                    new ResourceType<>(
                            rtc.getName(),
                            Class.forName(rtc.getType())
                    )
            );
        }
        return resourceTypes.toArray(new ResourceType[resourceTypes.size()]);
    }

    public ResourceDeclaration resourceDeclaration() throws Exception{
        final ResourceType<?>[] dependingList = createResourceTypes(dependingResourceTypes);
        return SimpleResourceDeclaration.withRequired(dependingList);
    }
}
