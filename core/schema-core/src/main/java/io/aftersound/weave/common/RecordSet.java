package io.aftersound.weave.common;

import java.util.Collection;
import java.util.Map;

public class RecordSet {

    /**
     * RecordSet level context
     */
    private final Context context = new Context();

    private final Collection<Map<String, Object>> records;

    private RecordSet(Collection<Map<String, Object>> records) {
        this.records = records;
    }

    public static RecordSet from(Collection<Map<String, Object>> records) {
        return new RecordSet(records);
    }

    public <T> RecordSet withCtxObj(Key<T> key, T obj) {
        context.set(key, obj);
        return this;
    }

    public Collection<Map<String, Object>> records() {
        return records;
    }

    public <T> T getCtxObj(Key<T> key) {
        return context.get(key);
    }

}
