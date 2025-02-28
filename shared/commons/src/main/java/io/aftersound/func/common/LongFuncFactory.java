package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.TypeHelper;
import io.aftersound.util.TreeNode;

import java.util.*;

import static io.aftersound.func.FuncHelper.createCreationException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class LongFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor.builder("LONG", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("LONG:BAND", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("LONG:BNOT", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("LONG:BOR", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("LONG:BNOT", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("LONG:BXOR", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("LONG:BNOT", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("LONG:FROM", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("LONG:LIST:FROM", "TBD", "TBD")
//                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        switch (funcName) {
            case "LONG": {
                return createLiteralFunc(spec);
            }
            case "LONG:BAND": {
                return createBitwiseAndFunc(spec);
            }
            case "LONG:BNOT": {
                return createBitwiseNotFunc(spec);
            }
            case "LONG:BOR": {
                return createBitwiseOrFunc(spec);
            }
            case "LONG:BXOR": {
                return createBitwiseXorFunc(spec);
            }
            case "LONG:EQ": {
                return createEQFunc(spec);
            }
            case "LONG:FROM": {
                return createFromFunc(spec);
            }
            case "LONG:GE": {
                return createGEFunc(spec);
            }
            case "LONG:GT": {
                return createGTFunc(spec);
            }
            case "LONG:LE": {
                return createLEFunc(spec);
            }
            case "LONG:LT": {
                return createLTFunc(spec);
            }
            case "LONG:WITHIN": {
                return createWithinFunc(spec);
            }
            case "LIST<LONG>:FROM":{
                return createListFrom(spec);
            }
        }

        return null;
    }

    static class BitwiseAndFunc extends AbstractFuncWithHints<Long, Long> {

        private final long mask;

        public BitwiseAndFunc(long mask) {
            this.mask = mask;
        }

        @Override
        public Long apply(Long source) {
            return source != null ? (source & mask) : null;
        }

    }

    static class BitwiseNotFunc extends AbstractFuncWithHints<Long, Long> {

        @Override
        public Long apply(Long source) {
            return source != null ? (~source) : null;
        }

    }

    static class BitwiseOrFunc extends AbstractFuncWithHints<Long, Long> {

        private final long mask;

        public BitwiseOrFunc(long mask) {
            this.mask = mask;
        }

        @Override
        public Long apply(Long source) {
            return source != null ? (source | mask) : null;
        }

    }

    static class BitwiseXorFunc extends AbstractFuncWithHints<Long, Long> {

        private final long mask;

        public BitwiseXorFunc(long mask) {
            this.mask = mask;
        }

        @Override
        public Long apply(Long source) {
            return source != null ? (source ^ mask) : null;
        }

    }

    static class EQFunc extends AbstractFuncWithHints<Long, Boolean> {

        private final long value;

        public EQFunc(long value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Long source) {
            return source != null && source == value;
        }

    }

    static class FromFunc extends AbstractFuncWithHints<Object, Long> {

        @Override
        public Long apply(Object source) {
            if (source instanceof Date) {
                return ((Date) source).getTime();
            } else if (source instanceof Number) {
                return ((Number) source).longValue();
            } else if (source instanceof String) {
                return Long.parseLong((String) source);
            } else {
                return null;
            }
        }

    }

    static class FromDateFunc extends AbstractFuncWithHints<Date, Long> {

        @Override
        public Long apply(Date source) {
            return source != null ? source.getTime(): null;
        }

    }

    static class FromStringFunc extends AbstractFuncWithHints<String, Long> {

        @Override
        public Long apply(String source) {
            return source != null ? Long.parseLong(source) : null;
        }

    }

    static class FromNumberFunc extends AbstractFuncWithHints<Number, Long> {

        @Override
        public Long apply(Number source) {
            return source != null ? source.longValue() : null;
        }

    }

    static abstract class FromList<S> extends AbstractFuncWithHints<List<S>, List<Long>> {

        @Override
        public final List<Long> apply(List<S> source) {
            if (source != null) {
                List<Long> values = new ArrayList<>(source.size());
                for (S e : source) {
                    values.add(toLong(e));
                }
                return values;
            }
            return null;
        }

        protected abstract Long toLong(S e);

    }

    static class FromNumberList<N extends Number> extends FromList<N> {

        @Override
        protected Long toLong(N s) {
            if (s != null) {
                return s.longValue();
            } else {
                return null;
            }
        }

    }

    static class FromStringList extends FromList<String> {

        @Override
        protected Long toLong(String s) {
            if (s != null) {
                return Long.parseLong(s);
            } else {
                return null;
            }
        }

    }

    static class GEFunc extends AbstractFuncWithHints<Long, Boolean> {

        private final long value;

        public GEFunc(long value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Long source) {
            return source != null && source >= value;
        }

    }

    static class GTFunc extends AbstractFuncWithHints<Long, Boolean> {

        private final long value;

        public GTFunc(long value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Long source) {
            return source != null && source > value;
        }

    }

    static class LEFunc extends AbstractFuncWithHints<Long, Boolean> {

        private final long value;

        public LEFunc(long value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Long source) {
            return source != null && source <= value;
        }

    }

    static class LTFunc extends AbstractFuncWithHints<Long, Boolean> {

        private final long value;

        public LTFunc(long value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Long source) {
            return source != null && source < value;
        }

    }

    static class LiteralFunc extends AbstractFuncWithHints<String, Long> {

        private final long value;

        public LiteralFunc(long value) {
            this.value = value;
        }

        @Override
        public Long apply(String str) {
            return value;
        }

    }

    static class WithinFunc extends AbstractFuncWithHints<Long, Boolean> {

        private final long lowerBound;
        private final long upperBound;
        private final boolean lowerInclusive;
        private final boolean upperInclusive;

        public WithinFunc(long lowerBound, long upperBound, boolean lowerInclusive, boolean upperInclusive) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.lowerInclusive = lowerInclusive;
            this.upperInclusive = upperInclusive;
        }

        @Override
        public Boolean apply(Long value) {
            if (value != null) {
                return  ((lowerInclusive && value >= lowerBound) || (!lowerInclusive && value > lowerBound)) &&
                        ((upperInclusive && value <= upperBound) || (!upperInclusive && value < upperBound));
            } else {
                return Boolean.FALSE;
            }
        }

    }

    private Func createBitwiseAndFunc(TreeNode spec) {
        final long mask = Long.parseLong(spec.getDataOfChildAt(0));
        return new BitwiseAndFunc(mask);
    }

    private Func createBitwiseNotFunc(TreeNode spec) {
        return new BitwiseNotFunc();
    }

    private Func createBitwiseOrFunc(TreeNode spec) {
        final long mask = Long.parseLong(spec.getDataOfChildAt(0));
        return new BitwiseOrFunc(mask);
    }

    private Func createBitwiseXorFunc(TreeNode spec) {
        final long mask = Long.parseLong(spec.getDataOfChildAt(0));
        return new BitwiseXorFunc(mask);
    }

    private Func createFromFunc(TreeNode spec) {
        // LONG:FROM(<sourceType>)
        final String sourceType = spec.getDataOfChildAt(0);

        if (sourceType == null) {
            return new FromFunc();
        }

        // LONG:FROM(Date)
        if (ProtoTypes.DATE.matchIgnoreCase(sourceType)) {
            return new FromDateFunc();
        }

        // LONG:FROM(String)
        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringFunc();
        }

        // LONG:FROM(Double|Float|Integer|Long|Short) or simply LONG:FROM(Number)
        if (TypeHelper.isNumberType(sourceType) || "number".equalsIgnoreCase(sourceType)) {
            return new FromNumberFunc();
        }

        throw new CreationException(sourceType + " specified in value function spec as source type is not supported");
    }

    private Func createEQFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new EQFunc(Long.parseLong(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "LONG:EQ(literal)",
                    "LONG:EQ(10)"
            );
        }
    }

    private Func createGEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new GEFunc(Long.parseLong(v));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "LONG:GE(value)",
                    "LONG:GE(10)"
            );
        }
    }

    private Func createGTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new GTFunc(Long.parseLong(v));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "LONG:GT(value)",
                    "LONG:GT(10)"
            );
        }
    }

    private Func createLEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new LEFunc(Long.parseLong(v));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "LONG:LE(value)",
                    "LONG:LE(10)"
            );
        }
    }

    private Func createLTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new LTFunc(Long.parseLong(v));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "LONG:LT(value)",
                    "LONG:LT(10)"
            );
        }
    }

    private Func createWithinFunc(TreeNode spec) {
        // LONG:WITHIN(lowerBound,upperBound,I|E,I|E)

        final String l = spec.getDataOfChildAt(0);
        final String u = spec.getDataOfChildAt(1);
        final String li = spec.getDataOfChildAt(2, "I");
        final String ui = spec.getDataOfChildAt(3, "I");

        try {
            long lowerBound = Long.parseLong(l);
            long upperBound = Long.parseLong(u);

            if (!("I".equalsIgnoreCase(li) || "E".equalsIgnoreCase(li))) {
                throw new Exception("Only [I,i,E,e] are supported inclusive/exclusive indicator");
            }
            boolean lowerInclusive = "I".equalsIgnoreCase(li);

            if (!("I".equalsIgnoreCase(ui) || "E".equalsIgnoreCase(ui))) {
                throw new Exception("Only [I,i,E,e] are supported inclusive/exclusive indicator");
            }
            boolean upperInclusive = "I".equalsIgnoreCase(ui);

            return new WithinFunc(lowerBound, upperBound, lowerInclusive, upperInclusive);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "LONG:WITHIN(lowerBound,upperBound,I|E,I|E)",
                    "LONG:WITHIN(1,100) or LONG:WITHIN(1,100,E,E) or LONG:WITHIN(1,100,I,E)"
            );
        }
    }

    private Func createLiteralFunc(TreeNode spec) {
        // LONG(literal)
        final String literal = spec.getDataOfChildAt(0);
        try {
            long value = Long.parseLong(literal);
            return new LiteralFunc(value);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "LONG(value)",
                    "LONG(10)"
            );
        }
    }

    private Func createListFrom(TreeNode spec) {
        String sourceType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringList();
        }

        if (TypeHelper.isNumberType(sourceType) || "number".equalsIgnoreCase(sourceType)) {
            return new FromNumberList();
        }

        throw FuncHelper.createCreationException(
                spec,
                "LIST<LONG>:FROM(sourceType)",
                "LIST<LONG>:FROM(string)",
                new Exception(String.format("Specified sourceType '%s' is not supported", sourceType))
        );
    }

}
