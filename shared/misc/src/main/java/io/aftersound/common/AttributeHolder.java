package io.aftersound.common;

import java.util.Collection;
import java.util.Map;

public class AttributeHolder<H extends AttributeHolder<H>> {

    private Map<String, Object> attributes;

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public AttributeHolder<H> acquire(AttributeHolder<H> that) {
        this.attributes.putAll(that.attributes);
        return this;
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

    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    public Collection<String> keys() {
        return attributes.keySet();
    }

    public <ATTR> H set(Key<ATTR> key, ATTR value) {
        attributes.put(key.name(), value);
        return (H) this;
    }

}
