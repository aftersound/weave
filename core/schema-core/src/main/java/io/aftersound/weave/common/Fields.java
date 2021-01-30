package io.aftersound.weave.common;

import java.util.*;

/**
 * Helper which wraps around a list of {@link Field}s and provides easy access/lookup
 */
public final class Fields {

    private final List<Field> fields;
    private final Map<String, Field> fieldByName;

    private Fields(List<Field> fields) {
        if (fields != null) {
            this.fields = Collections.unmodifiableList(fields);
            Map<String, Field> fieldByName = new LinkedHashMap<>(fields.size());
            for (Field field : fields) {
                fieldByName.put(field.getName(), field);
            }
            this.fieldByName = Collections.unmodifiableMap(fieldByName);
        } else {
            this.fields = Collections.emptyList();
            this.fieldByName = Collections.emptyMap();
        }
    }

    public static Fields of(List<Field> fields) {
        return new Fields(fields);
    }

    public Field getField(String fieldName) {
        return fieldByName.get(fieldName);
    }

}
