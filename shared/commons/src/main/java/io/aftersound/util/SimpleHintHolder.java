package io.aftersound.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple implementation of {@link HintHolder}
 */
public class SimpleHintHolder implements HintHolder {

    private final Map<String, String> hints = new HashMap<>();

    private transient boolean locked;

    @Override
    public void addHint(String id, String value) {
        if (!locked) {
            hints.put(id, value);
        }
    }

    @Override
    public void addAll(Map<String, String> hints) {
        if (!locked && hints != null) {
            this.hints.putAll(hints);
        }
    }

    @Override
    public boolean hasHint(String id, String value) {
        return value.equals(hints.get(id));
    }

    @Override
    public void lockHints() {
        this.locked = true;
    }

}
