package io.aftersound.weave.service.security;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.security.Authentication;
import io.aftersound.weave.security.AuthenticationControl;
import io.aftersound.weave.security.Authenticator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AuthenticatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticatorFactory.class);

    private final Map<String, Authenticator> authenticatorByTypeName = new HashMap<>();

    public AuthenticatorFactory(ActorBindings<AuthenticationControl, Authenticator, Authentication> authenticatorBindings) {
        ActorFactory<AuthenticationControl, Authenticator, Authentication> authActorFactory = new ActorFactory<>(authenticatorBindings);
        for (NamedType<AuthenticationControl> type : authenticatorBindings.controlTypes().all()) {
            try {
                Authenticator authenticator = authActorFactory.createActor(type.name());
                authenticatorByTypeName.put(type.name(), authenticator);
            } catch (Exception e) {
                LOGGER.error("{} occurred when trying to create Authenticator for type {}", e, type.name());
            }
        }
    }

    public Authenticator getAuthenticator(AuthenticationControl control) {
        return authenticatorByTypeName.get(control.getType());
    }

}
