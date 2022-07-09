package io.aftersound.weave.actor;

import java.util.List;

public class ActorBindingsConfig {

    /**
     * the group name of actor bindings
     */
    private String group;

    /**
     * base actor type/class
     */
    private String baseType;

    /**
     * types/classes which extends base actor type
     */
    private List<String> types;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    public List<String> getTypes() {
        return types;
    }

    public void setTypes(List<String> types) {
        this.types = types;
    }

}
