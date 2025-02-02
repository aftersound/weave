package io.aftersound.func.common;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.Directive;
import io.aftersound.func.FuncWithHints;
import io.aftersound.schema.Field;
import io.aftersound.schema.Schema;
import io.aftersound.schema.Type;
import io.aftersound.schema.TypeHelper;

import java.util.*;

public class ParseMapFromFunc<IN> extends AbstractFuncWithHints<IN, Map<String, Object>> {

    private final Schema schema;

    public ParseMapFromFunc(Schema schema) {
        this.schema = schema;
    }

    @Override
    public Map<String, Object> apply(IN source) {
        return parse(source, schema.getFields());
    }

    private Map<String, Object> parse(Object source, List<Field> fields) {
        if (source == null) {
            return null;
        }

        Map<String, Object> m = new LinkedHashMap<>();
        for (Field field : fields) {
            final String fieldName = field.getName();
            final Type type = field.getType();
            Directive directive = field.directives().first(d -> "T".equals(d.getCategory()));
            FuncWithHints<Object, Object> func = (FuncWithHints<Object, Object>) directive.function();
            final Object v;
            if (func.hasHint("ON", "TARGET")) {
                v = func.apply(m);
            } else {
                v = func.apply(source);
            }

            if (TypeHelper.isPrimitive(type)) {
                m.put(fieldName, v);
            } else if (TypeHelper.isObject(type)) {
                m.put(fieldName, parse(v, type.getFields()));
            } else if (TypeHelper.isArray(type)) {
                m.put(fieldName, parseList(v, type.getElementType()));
            } else if (TypeHelper.isList(type)) {
                m.put(fieldName, parseList(v, type.getElementType()));
            } else if (TypeHelper.isList(type)) {
                m.put(fieldName, parseSet(v, type.getElementType()));
            }
        }

        return m;
    }

    private List<Object> parseList(Object o, Type elementType) {
        List<Object> list = new ArrayList<>();
        mapCollection(o, list, elementType);
        return list;
    }

    private Set<?> parseSet(Object o, Type elementType) {
        Set<Object> set = new LinkedHashSet<>();
        mapCollection(o, set, elementType);
        return set;
    }

    private void mapCollection(Object source, Collection<Object> target, Type elementType) {
        if (!(source instanceof Iterable<?>)) {
            return;
        }
        Iterator<?> iter = ((Iterable<?>) source).iterator();
        if (TypeHelper.isPrimitive(elementType)) {
            while (iter.hasNext()) {
                target.add(iter.next());
            }
        } else if (TypeHelper.isObject(elementType)) {
            while (iter.hasNext()) {
                Object e = iter.next();
                target.add(parse(e, elementType.getFields()));
            }
        }
        // TODO other element types
    }

}
