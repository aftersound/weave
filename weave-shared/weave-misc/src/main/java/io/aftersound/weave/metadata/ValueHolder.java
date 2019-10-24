package io.aftersound.weave.metadata;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValueHolder {

    private ValueMetadata metdata;
    private Object value;

    public ValueHolder(ValueMetadata metadata, Object value) {
        this.metdata = metadata;
        this.value = value;
    }

    public ValueMetadata metadata() {
        return metdata;
    }

    public Object getValue() {
        return value;
    }

    public <T> T singleValue(Class<T> expectedType) {
        if (value instanceof List<?>) {
            List<?> valueList = (List<?>) value;
            Object first = !valueList.isEmpty() ? valueList.get(0) : null;
            return expectedType.isInstance(first) ? expectedType.cast(first) : null;
        } else {
            return expectedType.isInstance(value) ? expectedType.cast(value) : null;
        }
    }

    public <T> List<T> multiValues(final Class<T> expectedType) {
        if (value instanceof List<?>) {
            List<?> values = (List<?>) value;
            return (List<T>)values;
        } else {
            return expectedType.isInstance(value) ? Arrays.asList(expectedType.cast(value))
                    : Collections.<T>emptyList();
        }
    }
}
