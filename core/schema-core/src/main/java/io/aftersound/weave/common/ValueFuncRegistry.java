package io.aftersound.weave.common;

import io.aftersound.weave.utils.Factory;
import io.aftersound.weave.utils.Registry;

/**
 * A {@link Registry} which
 *  1.creates {@link ValueFunc} in according to value function spec
 *  2.caches created {@link ValueFunc}
 *  3.makes {@link ValueFunc} available when value function spec is given
 */
public final class ValueFuncRegistry extends Registry<String, ValueFunc<?, ?>> {

    private final Factory<String, ValueFunc<?, ?>> factory = new Factory<String, ValueFunc<?, ?>>() {
        @Override
        public ValueFunc<?, ?> create(String valueFuncSpec) {
            return MasterValueFuncFactory.create(valueFuncSpec);
        }
    };

    /**
     * Get {@link ValueFunc} which acts in according to given value function specification
     *  1.If exists, simply return cached one
     *  2.If not exists, create one with {@link MasterValueFuncFactory}, cache it then return
     *
     * @param valueFuncSpec            value function specification
     * @param <SOURCE>                 generic type of source/input
     * @param <TARGET>                 generic type of target/output
     * @return a {@link ValueFunc} which acts in according to given value function specification
     */
    public <SOURCE, TARGET> ValueFunc<SOURCE, TARGET> getValueFunc(final String valueFuncSpec) {
        return (ValueFunc<SOURCE, TARGET>) get(valueFuncSpec, factory);
    }

}
