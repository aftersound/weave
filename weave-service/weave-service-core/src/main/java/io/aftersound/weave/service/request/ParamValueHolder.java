package io.aftersound.weave.service.request;

import io.aftersound.weave.metadata.ValueHolder;
import io.aftersound.weave.metadata.ValueMetadata;
import io.aftersound.weave.service.metadata.param.ParamField;

import java.util.List;

/**
 * Holder of a parameter value
 */
public class ParamValueHolder extends ValueHolder {

    private String paramName;
    private List<String> rawValues;

    private ParamValueHolder(String paramName, ValueMetadata valueMetadata, Object value) {
        super(valueMetadata, value);
        this.paramName = paramName;
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
        return new ParamValueHolder(paramField.getName(), ValueMetadata.multiValued(null, paramField.getValueType()), value);
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

    public List<String> getRawValues() {
        return rawValues;
    }
    public ParamValueHolder bindRawValues(List<String> rawValues) {
        this.rawValues = rawValues;
        return this;
    }

}
