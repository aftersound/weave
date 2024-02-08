package io.aftersound.weave.common;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * {@link ValueFunc} which parses source into an object of specified class,
 * in according to given {@link Schema}
 *
 * @param <S> generic type of source record
 */
public class ParseAsObjectFunc<S> extends ValueFunc<S, Object> {

    private final ParseAsMapFunc<S> parseAsMapFunc;
    private final ObjectCreator objectCreator;

    public ParseAsObjectFunc(String type, Schema schema) {
        try {
            Fields fields = Fields.from(schema.getFields());

            Class<?> cls = Class.forName(type);
            this.parseAsMapFunc = new ParseAsMapFunc<>(fields);
            this.objectCreator = new ObjectCreator(cls, fields.getFieldNames());
        } catch (Exception e) {
            throw new ValueFuncException("Failed to construct ParseAsObjectFunc", e);
        }
    }

    public ParseAsObjectFunc(Class<?> type, Schema schema) {
        try {
            Fields fields = Fields.from(schema.getFields());
            this.parseAsMapFunc = new ParseAsMapFunc<>(fields);
            this.objectCreator = new ObjectCreator(type, fields.getFieldNames());
        } catch (Exception e) {
            throw new ValueFuncException("Failed to construct ParseAsObjectFunc", e);
        }
    }

    @Override
    public Object apply(S source) {
        Map<String, Object> m = parseAsMapFunc.apply(source);
        return m != null ? objectCreator.create(m) : null;
    }

    private static class ObjectCreator {

        private final Class<?> type;
        private final Map<String, Method> setterByFieldName;

        public ObjectCreator(Class<?> type, Collection<String> fieldNames) {
            this.type = type;

            Map<String, Method> setters = new HashMap<>();
            for (Method m : type.getDeclaredMethods()) {
                String methodName = m.getName();
                if (!Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()) && methodName.startsWith("set")) {
                    String fieldName = m.getName().substring("set".length());
                    fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                    setters.put(fieldName, m);
                }
            }

            Map<String, Method> setterByFieldName = new HashMap<>(fieldNames.size());
            for (String fieldName : fieldNames) {
                Method setter = setters.get(fieldName);
                if (setter != null) {
                    setterByFieldName.put(fieldName, setter);
                } else {
                    String msg = String.format("'%s' has no public setter for field '%s'", type.getName(), fieldName);
                    throw new IllegalArgumentException(msg);
                }
            }
            this.setterByFieldName = Collections.unmodifiableMap(setterByFieldName);
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
