package io.aftersound.util;

public class KeyFilterFactory {

    public static final KeyFilter ANY = key -> true;

    public static <A> KeyFilter keyWithAttribute(Key<A> attrKey, A attrValue) {
        return key -> key.hasAttribute(attrKey, attrValue);
    }

}
