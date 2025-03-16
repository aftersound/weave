package io.aftersound.common;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Helper that holds types derived from same base type together and their names
 * @param <T>
 *          - base type in generic
 */
public class NamedTypes<T> {

    private Map<String, NamedType<T>> typeByName = new LinkedHashMap<>();

    public void include(NamedType<T> namedType) {
        typeByName.put(namedType.name(), namedType);
    }

    public Collection<String> names() {
        return typeByName.keySet();
    }

    public NamedType<T> get(String name) {
        return typeByName.get(name);
    }

    public Collection<NamedType<T>> all() {
        return typeByName.values();
    }

    public NamedTypes<T> readOnly() {
        NamedTypes<T> namedTypes = new NamedTypes<>();
        namedTypes.typeByName = Collections.unmodifiableMap(typeByName);
        return namedTypes;
    }
}
