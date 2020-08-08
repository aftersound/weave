package io.aftersound.weave.common;

/**
 * Conceptual parser which parses field value from source
 * @param <T>
 *          target type of field in generic form
 * @param <S>
 *          source that holds data for given field
 */
public interface FieldValueParser<T, S> {

    /**
     * Parse field value from given source
     * @param field
     *          field metadata
     * @param source
     *          source of generic type S
     * @return value
     */
    T parse(Field field, S source);
}
