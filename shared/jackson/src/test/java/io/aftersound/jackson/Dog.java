package io.aftersound.jackson;

import io.aftersound.common.NamedType;

public class Dog implements Animal {

    public static final NamedType<Animal> TYPE = NamedType.of("Dog", Dog.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

    private boolean canBark;

    public boolean isCanBark() {
        return canBark;
    }

    public void setCanBark(boolean canBark) {
        this.canBark = canBark;
    }
}
