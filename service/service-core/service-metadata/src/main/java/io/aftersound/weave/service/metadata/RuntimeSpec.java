package io.aftersound.weave.service.metadata;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.component.ComponentConfig;

import java.util.List;

/**
 * The metadata/specification about a service app runtime.
 */
public class RuntimeSpec {

    private Info info;
    private List<ActorBindingsConfig> extensions;
    private List<ComponentConfig> components;
    private List<ServiceMetadata> services;

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public List<ActorBindingsConfig> getExtensions() {
        return extensions;
    }

    public void setExtensions(List<ActorBindingsConfig> extensions) {
        this.extensions = extensions;
    }

    public List<ComponentConfig> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentConfig> components) {
        this.components = components;
    }

    public List<ServiceMetadata> getServices() {
        return services;
    }

    public void setServices(List<ServiceMetadata> services) {
        this.services = services;
    }

}
