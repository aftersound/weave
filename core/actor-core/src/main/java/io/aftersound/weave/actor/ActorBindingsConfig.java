package io.aftersound.weave.actor;

import java.util.List;

public class ActorBindingsConfig {

    /**
     * a scenario that needs actor binding
     */
    private String scenario;

    /**
     * base actor type/class
     */
    private String baseType;

    /**
     * types/classes which extends base actor type
     */
    private List<String> extensionTypes;

    public String getScenario() {
        return scenario;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public List<String> getExtensionTypes() {
        return extensionTypes;
    }

    public void setExtensionTypes(List<String> extensionTypes) {
        this.extensionTypes = extensionTypes;
    }

}
