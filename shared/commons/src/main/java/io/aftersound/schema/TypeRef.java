package io.aftersound.schema;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeRef<T> implements Comparable<TypeRef<T>> {

    protected final Type _type;

    protected TypeRef() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
        } else {
            this._type = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        }
    }

    public Type getType() {
        return this._type;
    }

    public int compareTo(TypeRef<T> o) {
        return 0;
    }

}
