package io.aftersound.jackson;

import io.aftersound.common.NamedType;

public class Cat implements Animal {

    public static final NamedType<Animal> TYPE = NamedType.of("Cat", Cat.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

    private boolean canMeow;

    public boolean isCanMeow() {
        return canMeow;
    }

    public void setCanMeow(boolean canMeow) {
        this.canMeow = canMeow;
    }
}
