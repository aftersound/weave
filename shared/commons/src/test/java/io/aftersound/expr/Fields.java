package io.aftersound.expr;

import io.aftersound.dict.Dictionary;
import io.aftersound.schema.Field;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Fields implements Dictionary<Field> {

    private final List<Field> fields;
    private final Map<String, Field> byName;

    public Fields(Field... fields) {
        this.fields = List.of(fields);
        this.byName = this.fields.stream().collect(Collectors.toMap(Field::getName, f -> f));
    }

    @Override
    public List<Field> all() {
        return fields;
    }

    @Override
    public Field byName(String name) {
        return byName.get(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <ATTR> ATTR getAttribute(String name, String attributeName) {
        Field field = byName(name);
        if (field != null) {
            if ("typeName".equals(attributeName)) {
                return (ATTR) field.getType().getName();
            }
        }
        return null;
    }

}
