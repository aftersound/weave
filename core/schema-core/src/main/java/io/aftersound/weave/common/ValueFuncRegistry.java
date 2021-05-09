package io.aftersound.weave.common;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.utils.Factory;
import io.aftersound.weave.utils.Registry;

/**
 * A {@link Registry} which
 *  1.creates {@link ValueFunc} in according to value function spec
 *  2.caches created {@link ValueFunc}
 *  3.makes {@link ValueFunc} available when value function spec is given
 */
public final class ValueFuncRegistry extends Registry<String, ValueFunc<?, ?>> {

    private final ActorRegistry<ValueFuncFactory> valueFuncFactoryRegistry;

    public ValueFuncRegistry(ActorRegistry<ValueFuncFactory> valueFuncFactoryRegistry) {
        super();
        this.valueFuncFactoryRegistry = valueFuncFactoryRegistry;
    }

    /**
     * Get {@link ValueFunc} which acts in according to given value function specification
     *  1.If exists, simply return cached one
     *  2.If not exists, create one with {@link ValueFuncFactory} available in registry, cache it then return
     *
     * @param valueFuncSpec            value function specification
     * @param <SOURCE>                 generic type of source/input
     * @param <TARGET>                 generic type of target/output
     * @return a {@link ValueFunc} which acts in according to given value function specification
     */
    public <SOURCE, TARGET> ValueFunc<SOURCE, TARGET> getValueFunc(final String valueFuncSpec) {
        return (ValueFunc<SOURCE, TARGET>) get(
                valueFuncSpec,
                new Factory<String, ValueFunc<?, ?>>() {

                    @Override
                    public ValueFunc<?, ?> create(String valueFuncSpec) {
                        if (valueFuncSpec == null || valueFuncSpec.isEmpty()) {
                            return null;
                        }
                        int index = valueFuncSpec.indexOf('(');
                        String valueFuncType = index >= 0 ? valueFuncSpec.substring(0, index) : valueFuncSpec;
                        ValueFuncFactory valueFuncFactory = valueFuncFactoryRegistry.get(valueFuncType);
                        if (valueFuncFactory == null) {
                            throw new ValueFuncException("ValueFuncFactory for " + valueFuncType + " is not loaded or does not exist");
                        }

                        // if identified ValueFuncFactory needs to be aware of ValueFunc factory registry
                        if (valueFuncFactory instanceof RegistryAwareValueFuncFactory) {
                            ((RegistryAwareValueFuncFactory) valueFuncFactory).setRegistry(ValueFuncRegistry.this);
                        }

                        return valueFuncFactory.createValueFunc(valueFuncSpec);
                    }
                }
        );
    }

}
