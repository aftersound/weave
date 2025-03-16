package io.aftersound.weave.service.rl;

import io.aftersound.component.ComponentRegistry;

/**
 * Rate limit evaluator decides whether a request should be
 * served or blocked based on rate limit algorithm/policy
 *
 * @param <REQUEST>
 */
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
     */
    public abstract RateLimitDecision evaluate(RateLimitControl control, REQUEST request);
}
