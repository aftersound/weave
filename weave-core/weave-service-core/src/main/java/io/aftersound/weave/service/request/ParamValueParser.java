package io.aftersound.weave.service.request;

import io.aftersound.weave.metadata.ValueParser;
import io.aftersound.weave.service.metadata.param.ParamField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Helper which parses raw parameter values of supported types
 */
public final class ParamValueParser {

    private ParamValueParser() {
    }

    public static ParamValueHolder parse(
            ParamField paramMetadata,
            String paramName,
            List<String> rawValues) {
        ValueParser valueParser = new ValueParserImpl(paramMetadata.getValueType());
        if (paramMetadata.isMultiValued()) {
            Object values = valueParser.parseMultiValues(rawValues);
            if (values != null) {
                return ParamValueHolder.multiValuedScoped(
                        paramMetadata.getType().name(),
                        paramName,
                        paramMetadata.getValueType(),
                        values
                ).bindRawValues(rawValues);
            }
        } else {
            Object value = valueParser.parseSingleValue(rawValues);
            if (value != null) {
                return ParamValueHolder.singleValuedScoped(
                        paramMetadata.getType().name(),
                        paramName,
                        paramMetadata.getValueType(),
                        value
                ).bindRawValues(rawValues);
            }
        }
        return null;
    }

    private static class ValueParserImpl implements ValueParser {

        private final String valueType;

        ValueParserImpl(String valueType) {
            this.valueType = valueType;
        }

        @Override
        public Object parseMultiValues(List<String> rawValues) {
            if (rawValues == null || rawValues.isEmpty()) {
                return Collections.emptyList();
            }

            if ("String".equals(valueType) || valueType == null) {
                return rawValues;
            }

            if ("Boolean".equals(valueType)) {
                return parseBooleanValues(rawValues);
            }

            if ("Integer".equals(valueType)) {
                return parseIntegerValues(rawValues);
            }

            if ("Long".equals(valueType)) {
                return parseLongValues(rawValues);
            }

            if ("Float".equals(valueType)) {
                return parseFloatValues(rawValues);
            }

            if ("Double".equals(valueType)) {
                return parseDoubleValues(rawValues);
            }

            return null;
        }

        @Override
        public Object parseSingleValue(List<String> rawValues) {
            if (rawValues == null || rawValues.isEmpty()) {
                return null;
            }

            if ("String".equals(valueType)) {
                return rawValues.get(0);
            }

            if ("Boolean".equals(valueType)) {
                return Boolean.parseBoolean(rawValues.get(0));
            }

            if ("Integer".equals(valueType)) {
                return Integer.parseInt(rawValues.get(0));
            }

            if ("Long".equals(valueType)) {
                return Long.parseLong(rawValues.get(0));
            }

            if ("Float".equals(valueType)) {
                return Float.parseFloat(rawValues.get(0));
            }

            if ("Double".equals(valueType)) {
                return Double.parseDouble(rawValues.get(0));
            }

            return null;
        }

        private List<Boolean> parseBooleanValues(List<String> rawValues) {
            List<Boolean> values = new ArrayList<>();
            for (String rawValue : rawValues) {
                values.add(Boolean.parseBoolean(rawValue));
            }
            return values;
        }

        private List<Integer> parseIntegerValues(List<String> rawValues) {
            List<Integer> values = new ArrayList<>();
            for (String rawValue : rawValues) {
                Integer value = Integer.parseInt(rawValue);
                values.add(value);
            }
            return values;
        }

        private List<Long> parseLongValues(List<String> rawValues) {
            List<Long> values = new ArrayList<>();
            for (String rawValue : rawValues) {
                Long value = Long.parseLong(rawValue);
                values.add(value);
            }
            return values;
        }

        private List<Float> parseFloatValues(List<String> rawValues) {
            List<Float> values = new ArrayList<>();
            for (String rawValue : rawValues) {
                Float value = Float.parseFloat(rawValue);
                values.add(value);
            }
            return values;
        }

        private List<Double> parseDoubleValues(List<String> rawValues) {
            List<Double> values = new ArrayList<>();
            for (String rawValue : rawValues) {
                Double value = Double.parseDouble(rawValue);
                values.add(value);
            }
            return values;
        }

    }

}
