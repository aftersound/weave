package io.aftersound.weave.common;

/**
 * NamedType associates a base class with a name
 * @param <T>
 *          - base class in generic
 */
public final class NamedType<T> {

    // Unique name of types under same taxonomy class
    private final String name;

    // Type class
    private final Class<? extends T> type;

    private <E extends T> NamedType(String name, Class<E> type) {
        if (name == null || type == null) {
            throw new IllegalArgumentException("neither name nor type could be null");
        }
        this.name = name;
        this.type = type;
    }

    public String name() {
        return name;
    }

    @SuppressWarnings("unchecked")
    public <E extends T> Class<E> type() {
        return (Class<E>) type;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof NamedType)) {
            return false;
        }

        NamedType<?> that = (NamedType<?>)o;

        if (name.equals(that.name) && type.equals(that.type)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("{")
                .append('"').append("name").append('"').append(':').append('"').append(name).append('"')
                .append(",")
                .append('"').append("type").append('"').append(':').append('"').append(type.getName()).append('"')
                .append("}")
                .toString();
    }

    public static <BT, T extends BT> NamedType<BT> of(String name, Class<T> type) {
        return new NamedType<>(name, type);
    }

}
