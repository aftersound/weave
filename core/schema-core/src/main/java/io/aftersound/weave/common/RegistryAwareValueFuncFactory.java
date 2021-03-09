package io.aftersound.weave.common;

import io.aftersound.weave.actor.ActorRegistry;

/**
 * A {@link ValueFuncFactory}, which creates a {@link ValueFunc} that combines
 * several sub {@link ValueFunc}, needs to access {@link ValueFuncFactory}s in
 * {@link ActorRegistry}
 */
public abstract class RegistryAwareValueFuncFactory extends ValueFuncFactory {

    protected ActorRegistry<ValueFuncFactory> valueFuncFactoryRegistry;

    final void setRegistry(ActorRegistry<ValueFuncFactory> valueFuncFactoryRegistry) {
        this.valueFuncFactoryRegistry = valueFuncFactoryRegistry;
    }

}
