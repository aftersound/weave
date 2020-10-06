package io.aftersound.weave.component;

class ComponentHandle<COMPONENT> {

    private final COMPONENT component;
    private final ComponentConfig config;

    private ComponentHandle(COMPONENT component, ComponentConfig config) {
        this.component = component;
        this.config = config;
    }

    static <COMPONENT> ComponentHandle of(COMPONENT component, ComponentConfig endpoint) {
        return new ComponentHandle(component, endpoint);
    }

    int optionsHash() {
        return config.getOptions().hashCode();
    }

    COMPONENT component() {
        return component;
    }

    ComponentConfig config() {
        return config;
    }
}
