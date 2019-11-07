package io.aftersound.weave.service.security;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.security.Authorization;
import io.aftersound.weave.security.AuthorizationControl;
import io.aftersound.weave.security.Authorizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class AuthorizerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizerFactory.class);

    private final Map<String, Authorizer> authorizerByTypeName = new HashMap<>();

    public AuthorizerFactory(ActorBindings<AuthorizationControl, Authorizer, Authorization> authorizerBindings) {
        ActorFactory<AuthorizationControl, Authorizer, Authorization> authActorFactory = new ActorFactory<>(authorizerBindings);
        for (NamedType<AuthorizationControl> type : authorizerBindings.controlTypes().all()) {
            try {
                Authorizer authorizer = authActorFactory.createActor(type.name());
                authorizerByTypeName.put(type.name(), authorizer);
            } catch (Exception e) {
                LOGGER.error("{} occurred when trying to create Authorizer for type {}", e, type.name());
            }
        }
    }

    public Authorizer getAuthorizer(AuthorizationControl control) {
        return authorizerByTypeName.get(control.getType());
    }

}
