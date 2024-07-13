package io.aftersound.weave.util.map;

public final class Key<T> {

    public static final Key<Filter> LIST_FILTER = new Key<>(Filter.class);
    public static final Key<Return> LIST_RETURN = new Key<>(Return.class);

    private final Class<T> type;

    private Key(Class<T> type) {
        this.type = type;
    }

    public Class<T> type() {
        return type;
    }

}
