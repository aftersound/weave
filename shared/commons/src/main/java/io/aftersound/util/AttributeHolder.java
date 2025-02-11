package io.aftersound.util;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class AttributeHolder {

    private Map<String, Object> attributes;

    public AttributeHolder() {
        attributes = new LinkedHashMap<>();
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public <T extends AttributeHolder> T acquire(AttributeHolder that) {
        attributes.putAll(that.attributes);
        return (T) this;
    }

    public <ATTR> ATTR get(Key<ATTR> key) {
        Object v = attributes.get(key.name());
        return key.type().isInstance(v) ? key.type().cast(v) : null;
    }

    public <ATTR> ATTR get(Key<ATTR> key, ATTR defaultValue) {
        Object v = attributes.get(key.name());
        return key.type().isInstance(v) ? key.type().cast(v) : defaultValue;
    }

    public <ATTR> boolean has(Key<ATTR> key) {
        return get(key) != null;
    }

    public <ATTR> boolean has(Key<ATTR> key, ATTR value) {
        ATTR v = get(key);
        if (v != null) {
            return v.equals(value);
        } else {
            return value == null;
        }
    }

    public boolean has(String key) {
        return attributes.containsKey(key);
    }

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    public Collection<String> keys() {
        return attributes.keySet();
    }

    public <T extends AttributeHolder, ATTR> T set(Key<ATTR> key, ATTR value) {
        attributes.put(key.name(), value);
        return (T) this;
    }

}
