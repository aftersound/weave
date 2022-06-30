package io.aftersound.weave.common;

/**
 * A fail-safe {@link ValueFunc} mainly for debugging purpose
 */
public class FailSafeValueFunc implements ValueFunc<Object, Object> {

    private final ValueFunc<Object, Object> delegate;

    public FailSafeValueFunc(String delegateValueFunc) {
        ValueFunc<Object, Object> delegate;
        try {
            delegate = MasterValueFuncFactory.create(delegateValueFunc);
        } catch (Exception e) {
            delegate = source -> source != null ? source.getClass().getSimpleName() + "@" + source.hashCode() : null;
        }
        this.delegate = delegate;
    }

    @Override
    public Object apply(Object source) {
        try {
            return delegate.apply(source);
        } catch (Exception e) {
            return source != null ? source.getClass().getSimpleName() + "@" + source.hashCode() : null;
        }
    }
}
