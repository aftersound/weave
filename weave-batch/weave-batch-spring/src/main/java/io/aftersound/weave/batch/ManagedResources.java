package io.aftersound.weave.batch;

import java.util.HashMap;

class ManagedResources {

    private final HashMap<String, Object> holder;

    ManagedResources() {
        this.holder = new HashMap<>();
    }

    <T> void set(String key, T value) {
        holder.put(key, value);
    }

    <T> T get(String key) {
        return (T) holder.get(key);
    }

}
