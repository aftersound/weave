package io.aftersound.weave.common;

import io.aftersound.common.Context;
import io.aftersound.util.Key;

import java.util.Collections;
import java.util.Map;

/**
 * A simple implementation of {@link Record} which wraps around a map
 */
public final class SimpleRecord implements Record {

    private final TypedField<?> primaryKey;
    private final Context ctx;
    private final Map<String, Object> r;

    public SimpleRecord(TypedField<?> primaryKey, Context context, Map<String, Object> fieldValues) {
        this.primaryKey = primaryKey;
        this.ctx = context;
        this.r = fieldValues != null ? fieldValues : Collections.<String, Object>emptyMap();
    }

    public SimpleRecord(Map<String, Object> fieldValues, Context context) {
        this(null, context, fieldValues);
    }

    public SimpleRecord(Map<String, Object> fieldValues) {
        // no context, and it's assumed the entity trying to access
        // this record will not make calls to getContextObject
        this(null, null, fieldValues);
    }

    @Override
    public <CO> CO getContextObject(Key<CO> ctxObjKey) {
        return ctx.get(ctxObjKey);
    }

    @Override
    public TypedField<?> getPrimaryKey() {
        return primaryKey;
    }

    @Override
    public Object get(String fieldName) {
        return r.get(fieldName);
    }

    @Override
    public <T> T get(TypedField<T> field) {
        return field.cast(r.get(field.getName()));
    }

    @Override
    public <T> T get(String fieldName, Typed<T> type) {
        return type.cast(r.get(fieldName));
    }

    @Override
    public <T> T get(String fieldName, Class<T> type) {
        Object v = r.get(fieldName);
        return type.isInstance(v) ? type.cast(v) : null;
    }

    @Override
    public <T> T get(ValueFunc<Record, T> valueFunc) {
        return valueFunc.apply(this);
    }

}
