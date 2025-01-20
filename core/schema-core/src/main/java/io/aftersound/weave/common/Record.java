package io.aftersound.weave.common;

import io.aftersound.util.Key;

/**
 * A conceptual record which
 *   1.might have a primary key
 *   2.might have context information matters to the entity accessing it
 *   3.more importantly, provides access to the values of fields
 */
public interface Record {

    /**
     * Get context object which matter to the entity accessing the record, if exists
     *
     * @param ctxObjKey the key of target context object
     * @param <CO> the expected type of target context object in generic form
     * @return the target context object
     */
    <CO> CO getContextObject(Key<CO> ctxObjKey);

    /**
     * Get primary key of this record
     *
     * @return the primary key in form of {@link TypedField} if this record has a primary key
     */
    TypedField<?> getPrimaryKey();

    /**
     * Get the value of the field with specified name.
     *
     * @param fieldName the name of target field
     * @return the value of the field with specified name
     */
    Object get(String fieldName);

    /**
     * Get the value of specified field
     *
     * @param field target field in {@link TypedField}
     * @param <T>   the expected value type of target field in generic form
     * @return the value of target field
     */
    <T> T get(TypedField<T> field);

    /**
     * Get the value of the field with specified name
     *
     * @param fieldName the name of target field
     * @param type      the expected type of target field
     * @param <T>       the expected type of target field in generic form
     * @return the value of the field with specified name
     */
    <T> T get(String fieldName, Typed<T> type);

    /**
     * Get the value of the field with specified name
     *
     * @param fieldName the name of target field
     * @param type      the expected type of target field in Class
     * @param <T>       the expected type of target field in generic form
     * @return the value of the field with specified name
     */
    <T> T get(String fieldName, Class<T> type);

    /**
     * Accept a {@link ValueFunc} to process this record and return its processed result
     *
     * @param valueFunc a {@link ValueFunc} which takes {@link Record} as input and produce target type
     * @param <T>       the type of value as processed result of given {@link ValueFunc}
     * @return the processed value
     */
    <T> T get(ValueFunc<Record, T> valueFunc);
}
