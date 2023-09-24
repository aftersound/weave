package io.aftersound.weave.common;

public final class TypedField<T> extends Field implements Typed<T> {

    private final Class<T> cls;

    public TypedField(String name, Class<T> cls) {
        super();
        super.setName(name);

        Type type = new Type();
        type.setName("class:" + cls.getName());
        super.setType(type);
        this.cls = cls;
    }

    public TypedField<T> func(String func) {
        super.setFunc(func);
        return this;
    }

    public TypedField<T> source(String source) {
//        super.setSource(source);
        return this;
    }

    public TypedField<T> description(String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    public Class<T> type() {
        return cls;
    }

    @Override
    public T cast(Object v) {
        return cls.isInstance(v) ? cls.cast(v) : null;
    }

}
