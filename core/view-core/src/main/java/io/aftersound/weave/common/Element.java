package io.aftersound.weave.common;

import java.util.Map;

public class Element {

    /**
     * name of this element
     */
    private String name;

    /**
     * multi-purpose versatile configuration
     */
    private Map<String, String> config;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

}
