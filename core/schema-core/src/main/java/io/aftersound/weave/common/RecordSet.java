package io.aftersound.weave.common;

import java.util.Collection;
import java.util.Map;

public class RecordSet<R> {

    /**
     * RecordSet level context
     */
    private final Context context = new Context();

    private final Collection<R> records;

    private RecordSet(Collection<R> records) {
        this.records = records;
    }

    public static <R> RecordSet<R> from(Collection<R> records) {
        return new RecordSet(records);
    }

    public <T> RecordSet<R> withCtxObj(Key<T> key, T obj) {
        context.set(key, obj);
        return this;
    }

    public Collection<R> records() {
        return records;
    }

    public <T> T getCtxObj(Key<T> key) {
        return context.get(key);
    }

}
