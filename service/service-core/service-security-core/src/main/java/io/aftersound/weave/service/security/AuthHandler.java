package io.aftersound.weave.service.security;

import io.aftersound.component.ComponentRegistry;

/**
 * Conceptual auth handler, which handles auth request in according to {@link AuthControl}
 * @param <BEARER> an entity bears authorization request
 */
public abstract class AuthHandler<BEARER> {

    protected ComponentRegistry componentRegistry;

    public final void setComponentRegistry(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }

    /**
     * @return type of this {@link AuthHandler}, must be identical to {@link AuthControl#getType()}
     */
    public abstract String getType();

    /**
     * Handle auth request in according to given {@link AuthControl}
     * @param control a concrete {@link AuthControl}
     * @param authorizationBearer an entity bears authorization request
     * @return an {@link Auth} which holds evaluation result
     * @throws SecurityException
     */
    public abstract Auth handle(AuthControl control, BEARER authorizationBearer) throws SecurityException;
}
