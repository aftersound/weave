package io.aftersound.weave.service.rl;

import io.aftersound.weave.component.ComponentRegistry;

public abstract class RateLimitEvaluator<REQUEST> {

    protected ComponentRegistry componentRegistry;

    public final void setComponentRegistry(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }

    /**
     * @return type of this {@link RateLimitEvaluator}, must be identical to {@link RateLimitControl#getType()}
     */
    public abstract String getType();

    /**
     * Evaluate whether given request should be rate limited
     *
     * @param control a concrete {@link RateLimitControl}
     * @param request a service request
     * @return an {@link RateLimitDecision} which holds the decision
     * @throws RateLimitException
     */
    public abstract RateLimitDecision evaluate(RateLimitControl control, REQUEST request) throws RateLimitException;
}
