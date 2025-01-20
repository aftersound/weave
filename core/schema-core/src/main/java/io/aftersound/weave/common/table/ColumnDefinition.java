package io.aftersound.weave.common.table;

import io.aftersound.util.AttributeHolder;
import io.aftersound.weave.common.Type;

import java.util.LinkedHashMap;
import java.util.Map;

public class ColumnDefinition extends AttributeHolder {

    private String group;
    private String displayName;
    private String name;
    private Type type;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public static Builder builder(String displayName, String name) {
        return new Builder(displayName, name);
    }

    public static class Builder {

        private final String displayName;
        private final String name;

        private Type type;

        private String group;
        private Map<String, Object> attributes;

        private Builder(String displayName, String name) {
            this.displayName = displayName;
            this.name = name;
        }

        private Builder withType(Type type) {
            this.type = type;
            return this;
        }

        public Builder withGroup(String group) {
            this.group = group;
            return this;
        }

        public Builder withAttributes(Map<String, Object> attributes) {
            if (attributes != null) {
                if (this.attributes == null) {
                    this.attributes = new LinkedHashMap<>();
                }
                this.attributes.putAll(attributes);
            }

            return this;
        }

        public Builder withAttribute(String name, Object value) {
            if (this.attributes == null) {
                this.attributes = new LinkedHashMap<>();
            }
            this.attributes.put(name, value);

            return this;
        }

        public ColumnDefinition build() {
            ColumnDefinition cd = new ColumnDefinition();
            cd.setGroup(group);
            cd.setDisplayName(displayName);
            cd.setName(name);
            cd.setType(type);
            cd.setAttributes(attributes);
            return cd;
        }
    }

}
