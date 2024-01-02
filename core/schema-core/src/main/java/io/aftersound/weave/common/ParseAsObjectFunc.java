package io.aftersound.weave.common;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ParseAsObjectFunc<S> extends ValueFunc<S, Object> {

    private final ParseAsMapFunc<S> parseAsMapFunc;
    private final ObjectBuilder objectBuilder;

    public ParseAsObjectFunc(String type, Schema schema) {
        try {
            Fields fields = Fields.from(schema.getFields());

            Class<?> cls = Class.forName(type);
            this.parseAsMapFunc = new ParseAsMapFunc<>(fields);
            this.objectBuilder = new ObjectBuilder(cls, fields.getFieldNames());
        } catch (Exception e) {
            throw new ValueFuncException("Failed to construct ParseAsObjectFunc", e);
        }
    }

    public ParseAsObjectFunc(Class<?> type, Schema schema) {
        try {
            Fields fields = Fields.from(schema.getFields());
            this.parseAsMapFunc = new ParseAsMapFunc<>(fields);
            this.objectBuilder = new ObjectBuilder(type, fields.getFieldNames());
        } catch (Exception e) {
            throw new ValueFuncException("Failed to construct ParseAsObjectFunc", e);
        }
    }

    @Override
    public Object apply(S source) {
        Map<String, Object> m = parseAsMapFunc.apply(source);
        return m != null ? objectBuilder.create(m) : null;
    }

    private static class ObjectBuilder {

        private final Class<?> type;
        private final Map<String, Method> setterByFieldName;

        public ObjectBuilder(Class<?> type, Collection<String> fieldNames) {
            this.type = type;
            this.setterByFieldName = new HashMap<>();
        }

        public Object create(Map<String, Object> m) {
            try {
                Object o = type.getDeclaredConstructor().newInstance();
                for (Map.Entry<String, Method> e : setterByFieldName.entrySet()) {
                    final String field = e.getKey();
                    final Method setter = e.getValue();
                    final Object fieldValue = m.get(field);
                    setter.invoke(o, fieldValue);
                }
                return o;
            } catch (Exception e) {
                throw new ValueFuncException("Failed to create object of ", e);
            }
        }


    }


}
