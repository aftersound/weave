package io.aftersound.component;

public class ComponentInfo {

    private String id;
    private String controlType;
    private String componentType;
    private ComponentConfig config;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public ComponentConfig getConfig() {
        return config;
    }

    public void setConfig(ComponentConfig config) {
        this.config = config;
    }
}
