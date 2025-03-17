package io.aftersound.sample.extension.service.service.security;

import io.aftersound.util.Key;
import io.aftersound.service.security.Auth;

public class DemoAuth implements Auth {

    private final String name;

    public DemoAuth(String name) {
        this.name = name;
    }

    @Override
    public String getType() {
        return DemoAuthControl.TYPE.name();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public <T> T get(Key<T> key) {
        return null;
    }

}
