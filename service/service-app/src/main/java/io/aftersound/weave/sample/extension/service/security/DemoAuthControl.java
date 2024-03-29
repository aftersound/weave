package io.aftersound.weave.sample.extension.service.security;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.security.AuthControl;

import java.util.Map;

public class DemoAuthControl implements AuthControl {

    public static final NamedType<AuthControl> TYPE = NamedType.of("Demo", DemoAuthControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

    private Map<String, String> userCredentials;

    public Map<String, String> getUserCredentials() {
        return userCredentials;
    }

    public void setUserCredentials(Map<String, String> userCredentials) {
        this.userCredentials = userCredentials;
    }

}
