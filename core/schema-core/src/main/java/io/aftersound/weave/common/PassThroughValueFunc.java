package io.aftersound.weave.common;

public final class PassThroughValueFunc implements ValueFunc<Object, Object> {

    public static final PassThroughValueFunc INSTANCE = new PassThroughValueFunc();

    private PassThroughValueFunc() {
    }

    @Override
    public Object apply(Object source) {
        return source;
    }

}
