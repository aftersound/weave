package io.aftersound.dict;

public class NullAttributeAccessor<E> implements AttributeAccessor<E> {

    @Override
    public <ATTR> ATTR get(E entry, String name) {
        // always return null
        return null;
    }

}
