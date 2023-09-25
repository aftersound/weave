package io.aftersound.weave.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link ValueFunc} which parses source into List as record, in according to
 * given {@link Schema}
 *
 * @param <S> generic type of source record
 */
public final class ParseAsListFunc<S> extends ValueFunc<S, List<Object>> {

    private final ValueFunc<S, Map<String, Object>> parseAsMapFunc;

    public ParseAsListFunc(Fields fields) {
        this.parseAsMapFunc = new ParseAsMapFunc<>(fields);
    }

    public ParseAsListFunc(Schema schema) {
        this.parseAsMapFunc = new ParseAsMapFunc<>(schema);
    }

    @Override
    public List<Object> apply(S source) {
        Map<String, Object> record = parseAsMapFunc.apply(source);
        return record != null ? new ArrayList(record.values()) : null;
    }

}
