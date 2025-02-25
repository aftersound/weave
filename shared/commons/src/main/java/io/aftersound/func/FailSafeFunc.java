package io.aftersound.func;

/**
 * A fail-safe {@link Func} mainly for debugging purpose
 */
public class FailSafeFunc extends AbstractFuncWithHints<Object, Object> {

    private final Func<Object, Object> delegate;

    public FailSafeFunc(Func<Object, Object> delegate) {
        assert delegate != null : "Delegate ValueFunc is null";
        this.delegate = delegate;
    }

    @Override
    public Object apply(Object source) {
        try {
            return delegate.apply(source);
        } catch (Exception e) {
            return null;
        }
    }
}
