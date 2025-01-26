package io.aftersound.dict;

/**
 * Attribute accessor of entries
 *
 * @param <E> - the type of entries whose attributes are accessible
 */
public interface AttributeAccessor<E> {

    /**
     * Get the value of attribute, with given name, of the entry
     *
     * @param entry  - the entry
     * @param name   - the name of the attribute
     * @param <ATTR> - the expected type of the attribute value
     * @return the value of the attribute
     */
    <ATTR> ATTR get(E entry, String name);
}
