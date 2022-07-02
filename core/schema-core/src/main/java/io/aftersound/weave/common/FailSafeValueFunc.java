package io.aftersound.weave.common;

import io.aftersound.weave.utils.TreeNode;

/**
 * A fail-safe {@link ValueFunc} mainly for debugging purpose
 */
public class FailSafeValueFunc implements ValueFunc<Object, Object> {

    private final ValueFunc<Object, Object> delegate;

    public FailSafeValueFunc(ValueFunc<Object, Object> delegate) {
        assert delegate != null : "Delegate ValueFunc is null";
        this.delegate = delegate;
    }

    public FailSafeValueFunc(String delegateValueFuncSpec) {
        this(createValueFunc(delegateValueFuncSpec));
    }

    public FailSafeValueFunc(TreeNode delegateValueFuncSpec) {
        this(createValueFunc(delegateValueFuncSpec));
    }

    private static ValueFunc<Object, Object> createValueFunc(String valueFuncSpec) {
        try {
            return MasterValueFuncFactory.create(valueFuncSpec);
        } catch (Exception e) {
            return PassThroughValueFunc.INSTANCE;
        }
    }

    private static ValueFunc<Object, Object> createValueFunc(TreeNode valueFuncSpec) {
        try {
            return MasterValueFuncFactory.create(valueFuncSpec);
        } catch (Exception e) {
            return PassThroughValueFunc.INSTANCE;
        }
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
