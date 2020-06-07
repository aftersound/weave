package io.aftersound.weave.service.runtime;

import io.aftersound.weave.resource.ResourceDeclaration;
import io.aftersound.weave.resource.ResourceType;

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
    private List<ResourceTypeConfig> shareableResourceTypes;
    private List<ResourceTypeConfig> resourceTypes;

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

    public List<ResourceTypeConfig> getShareableResourceTypes() {
        return shareableResourceTypes;
    }

    public void setShareableResourceTypes(List<ResourceTypeConfig> shareableResourceTypes) {
        this.shareableResourceTypes = shareableResourceTypes;
    }

    public List<ResourceTypeConfig> getResourceTypes() {
        return resourceTypes;
    }

    public void setResourceTypes(List<ResourceTypeConfig> resourceTypes) {
        this.resourceTypes = resourceTypes;
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
        final ResourceType<?>[] shareableList = createResourceTypes(shareableResourceTypes);
        final ResourceType<?>[] responsibilityList = createResourceTypes(resourceTypes);

        return new ResourceDeclaration() {

            @Override
            public ResourceType<?>[] getDependingResourceTypes() {
                return dependingList;
            }

            @Override
            public ResourceType<?>[] getShareableResourceTypes() {
                return shareableList;
            }

            @Override
            public ResourceType<?>[] getResourceTypes() {
                return responsibilityList;
            }

        };
    }
}
