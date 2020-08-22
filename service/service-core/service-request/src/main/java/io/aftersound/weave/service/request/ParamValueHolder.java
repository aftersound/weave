package io.aftersound.weave.service.request;

import io.aftersound.weave.service.metadata.param.ParamField;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Holder of a parameter value
 */
public class ParamValueHolder {

    private final String paramName;
    private final ValueMetadata metadata;
    private final Object value;

    private List<String> rawValues;

    private ParamValueHolder(String paramName, ValueMetadata valueMetadata, Object value) {
        this.paramName = paramName;
        this.metadata = valueMetadata;
        this.value = value;
    }

    /**
     * Create a multi-valued holder for given {@link ParamField} and value
     * @param paramField
     *          - a {@link ParamField} which defines a parameter
     * @param value
     *          - value of parameter
     * @return
     *          - value holder
     */
    public static ParamValueHolder multiValued(ParamField paramField, Object value) {
        return new ParamValueHolder(paramField.getName(), ValueMetadata.multiValued(null, paramField.getValueSpec()), value);
    }

    /**
     * Create a multi-valued holder for given {@link ParamField} and value in specified scope
     * @param scope
     *          - scope of parameter
     * @param paramName
     *          - name of parameter
     * @param valueType
     *          - value type
     * @param value
     *          - value
     * @return
     *          - value holder
     */
    public static ParamValueHolder multiValuedScoped(String scope, String paramName, String valueType, Object value) {
        return new ParamValueHolder(paramName, ValueMetadata.multiValued(scope, valueType), value);
    }

    /**
     * Create a single-valued holder
     * @param paramName
     *          - name of parameter
     * @param valueType
     *          - value type
     * @param value
     *          - value
     * @return
     *          - value holder
     */
    public static ParamValueHolder singleValued(String paramName, String valueType, Object value) {
        return new ParamValueHolder(paramName, ValueMetadata.singleValued(null, valueType), value);
    }

    /**
     * Create a single-valued holder for given {@link ParamField} and value in specified scope
     * @param scope
     *          - scope of parameter
     * @param paramName
     *          - name of parameter
     * @param valueType
     *          - value type
     * @param value
     *          - value
     * @return
     *          - value holder
     */
    public static ParamValueHolder singleValuedScoped(String scope, String paramName, String valueType, Object value) {
        return new ParamValueHolder(paramName, ValueMetadata.singleValued(scope, valueType), value);
    }

    public String getParamName() {
        return paramName;
    }

    public ValueMetadata metadata() {
        return metadata;
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

    public List<String> getRawValues() {
        return rawValues;
    }

    public ParamValueHolder bindRawValues(List<String> rawValues) {
        this.rawValues = rawValues;
        return this;
    }

}
