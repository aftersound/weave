package io.aftersound.weave.common;

public final class TypedField<T> extends Field implements Typed<T> {

    private final Class<T> type;

    public TypedField(String name, Class<T> type) {
        super();

        assert (name != null);
        assert (type != null);

        super.setName(name);
        super.setType(type.getName());
        this.type = type;
    }

    public TypedField<T> valueFunc(String valueFunc) {
        super.setValueFunc(valueFunc);
        return this;
    }

    public TypedField<T> source(String source) {
        super.setSource(source);
        return this;
    }

    public TypedField<T> description(String description) {
        super.setDescription(description);
        return this;
    }

    @Override
    public Class<T> type() {
        return type;
    }

    @Override
    public T cast(Object v) {
        return type.isInstance(v) ? type.cast(v) : null;
    }

}
