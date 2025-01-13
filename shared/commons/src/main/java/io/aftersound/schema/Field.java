package io.aftersound.schema;

import java.io.Serializable;

public class Field implements Serializable {

    private String name;
    private Type type;

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

    public static Builder stringFieldBuilder(String name) {
        return new Builder(name, TypeEnum.STRING.createType());
    }

    public static Builder integerFieldBuilder(String name) {
        return new Builder(name, TypeEnum.INTEGER.createType());
    }

    public static class Builder {

        private final String name;
        private final Type type;

        private Builder(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        public Field build() {
            Field f = new Field();
            f.setName(name);
            f.setType(type);
            return f;
        }

    }

}
