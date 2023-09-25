package io.aftersound.weave.common;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link ValueFunc} which parses source into Map as record, in according to
 * given {@link Schema}
 *
 * @param <S> generic type of source record
 */
public final class ParseAsMapFunc<S> extends ValueFunc<S, Map<String, Object>> {

    private final Fields fields;
    private final Collection<String> fieldNames;

    public ParseAsMapFunc(Fields fields) {
        this.fields = fields;
        this.fieldNames = fields.getFieldNames();
    }

    public ParseAsMapFunc(Schema schema) {
        this(Fields.from(schema.getFields()));
    }

    @Override
    public Map<String, Object> apply(S source) {
        Map<String, Object> record = new LinkedHashMap<>(fieldNames.size());
        for (String fieldName : fieldNames) {
            ValueFunc<Object, ?> valueFunc = fields.getValueFunc(fieldName);
            Object fieldValue;
            if (valueFunc.hasHint("ON", "TARGET")) {
                fieldValue = valueFunc.apply(record);
            } else {
                fieldValue = valueFunc.apply(source);
            }
            record.put(fieldName, fieldValue);
        }
        return record;
    }

}
