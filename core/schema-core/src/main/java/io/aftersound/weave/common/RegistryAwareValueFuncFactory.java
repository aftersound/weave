package io.aftersound.weave.common;

/**
 * A {@link ValueFuncFactory}, which creates a {@link ValueFunc} that combines
 * several sub {@link ValueFunc}, needs to access {@link ValueFuncRegistry}
 */
public abstract class RegistryAwareValueFuncFactory extends ValueFuncFactory {

    protected ValueFuncRegistry valueFuncRegistry;

    final void setRegistry(ValueFuncRegistry valueFuncRegistry) {
        this.valueFuncRegistry = valueFuncRegistry;
    }

}
