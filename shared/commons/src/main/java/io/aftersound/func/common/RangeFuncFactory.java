package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.util.Range;
import io.aftersound.util.TreeNode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class RangeFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = DescriptorHelper.getDescriptors(RangeFuncFactory.class);

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("RANGE:FROM".equals(funcName)) {
            return createFromFunc(spec);
        }

        return null;
    }

    static abstract class RangeFromFunc<E> extends AbstractFuncWithHints<String, Range<E>> {

        @Override
        public Range<E> apply(String encoded) {
            if (encoded == null || encoded.isEmpty()) {
                return null;
            }

            // position of comma
            int cp = encoded.indexOf(',');
            if (cp < 0) {
                return null;
            }

            Range<E> range = new Range<>();

            String lower = encoded.substring(0, cp);
            boolean lowerInclusive = false;
            if (lower.startsWith("[")) {
                lower = lower.substring(1);
                lowerInclusive = true;
            } else if (lower.startsWith("(")) {
                lower = lower.substring(1);
            }
            range.setLower(fromString(lower));
            range.setLowerInclusive(lowerInclusive);

            String upper = encoded.substring(cp + 1);
            boolean upperInclusive = false;
            if (upper.endsWith("]")) {
                upper = upper.substring(0, upper.length() - 1);
                upperInclusive = true;
            } else if (upper.endsWith(")")) {
                upper = upper.substring(0, upper.length() - 1);
            }
            range.setUpper(fromString(upper));
            range.setUpperInclusive(upperInclusive);

            return range;
        }

        protected abstract E fromString(String s);
    }

    static class DateRangeFromFunc extends RangeFromFunc<Date> {

        private final String dateFormat;

        protected DateRangeFromFunc(String dateFormat) {
            this.dateFormat = dateFormat;
        }

        @Override
        public Date fromString(String s) {
            if (s != null) {
                if ("Long".equals(dateFormat) || "LONG".equals(dateFormat)) {
                    return new Date(Long.parseLong(s));
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
                    try {
                        return sdf.parse(s);
                    } catch (ParseException e) {
                        throw new ExecutionException("cannot parse " + s + " with " + dateFormat, e);
                    }
                }
            } else {
                return null;
            }
        }

    }

    static class DoubleRangeFromFunc extends RangeFromFunc<Double> {
        @Override
        protected Double fromString(String s) {
            if (s != null) {
                return Double.parseDouble(s);
            } else {
                return null;
            }
        }
    }

    static class FloatRangeFromFunc extends RangeFromFunc<Float> {
        @Override
        protected Float fromString(String s) {
            if (s != null) {
                return Float.parseFloat(s);
            } else {
                return null;
            }
        }
    }

    static class IntegerRangeFromFunc extends RangeFromFunc<Integer> {
        @Override
        protected Integer fromString(String s) {
            if (s != null) {
                return Integer.parseInt(s);
            } else {
                return null;
            }
        }

    }

    static class LongRangeFromFunc extends RangeFromFunc<Long> {
        @Override
        public Long fromString(String s) {
            if (s != null) {
                return Long.parseLong(s);
            } else {
                return null;
            }
        }
    }

    static class ShortRangeFromFunc extends RangeFromFunc<Short> {
        @Override
        protected Short fromString(String s) {
            if (s != null) {
                return Short.parseShort(s);
            } else {
                return null;
            }
        }
    }

    static class StringRangeFromFunc extends RangeFromFunc<String> {
        @Override
        protected String fromString(String s) {
            return s;
        }
    }

    private Func createFromFunc(TreeNode spec) {
        final String valueType = spec.getDataOfChildAt(0);

        if (ProtoTypes.DATE.matchIgnoreCase(valueType)) {
            String dateFormat = spec.getChildAt(0).getDataOfChildAt(0);
            return new DateRangeFromFunc(dateFormat);
        }

        if (ProtoTypes.DOUBLE.matchIgnoreCase(valueType)) {
            return new DoubleRangeFromFunc();
        }

        if (ProtoTypes.FLOAT.matchIgnoreCase(valueType)) {
            return new FloatRangeFromFunc();
        }

        if (ProtoTypes.INTEGER.matchIgnoreCase(valueType)) {
            return new IntegerRangeFromFunc();
        }

        if (ProtoTypes.LONG.matchIgnoreCase(valueType)) {
            return new LongRangeFromFunc();
        }

        if (ProtoTypes.SHORT.matchIgnoreCase(valueType)) {
            return new ShortRangeFromFunc();
        }

        if (ProtoTypes.STRING.matchIgnoreCase(valueType)) {
            return new StringRangeFromFunc();
        }

        throw FuncHelper.createCreationException(
                spec,
                "RANGE:FROM(Date|Double|Float|Integer|Long|Short|String)",
                "RANGE:FROM(Double)"
        );
    }

}
