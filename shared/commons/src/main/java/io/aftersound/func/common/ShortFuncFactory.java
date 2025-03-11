package io.aftersound.func.common;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.Func;
import io.aftersound.func.FuncHelper;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.TypeHelper;
import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.List;

import static io.aftersound.func.FuncHelper.createCreationException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ShortFuncFactory extends MasterAwareFuncFactory {

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        switch (funcName) {
            case "SHORT": {
                return createLiteralFunc(spec);
            }
            case "SHORT:BAND": {
                return createBitwiseAndFunc(spec);
            }
            case "SHORT:BNOT": {
                return createBitwiseNotFunc(spec);
            }
            case "SHORT:BOR": {
                return createBitwiseOrFunc(spec);
            }
            case "SHORT:BXOR": {
                return createBitwiseXorFunc(spec);
            }
            case "SHORT:FROM": {
                return createFromFunc(spec);
            }
            case "SHORT:EQ": {
                return createEQFunc(spec);
            }
            case "SHORT:GE": {
                return createGEFunc(spec);
            }
            case "SHORT:GT": {
                return createGTFunc(spec);
            }
            case "SHORT:LE": {
                return createLEFunc(spec);
            }
            case "SHORT:LT": {
                return createLTFunc(spec);
            }
            case "LIST<SHORT>:FROM": {
                return createListFromFunc(spec);
            }
            case "SHORT:WITHIN": {
                return createWithinFunc(spec);
            }
            default: {
                return null;
            }
        }

    }

    static class BitwiseAndFunc extends AbstractFuncWithHints<Short, Short> {

        private final short mask;

        public BitwiseAndFunc(short mask) {
            this.mask = mask;
        }

        @Override
        public Short apply(Short source) {
            return source != null ? (short) (source & mask) : null;
        }

    }

    static class BitwiseNotFunc extends AbstractFuncWithHints<Short, Short> {

        @Override
        public Short apply(Short source) {
            return source != null ? (short) (~source) : null;
        }

    }

    static class BitwiseOrFunc extends AbstractFuncWithHints<Short, Short> {

        private final int mask;

        public BitwiseOrFunc(int mask) {
            this.mask = mask;
        }

        @Override
        public Short apply(Short source) {
            return source != null ? (short) (source | mask) : null;
        }

    }

    static class BitwiseXorFunc extends AbstractFuncWithHints<Short, Short> {

        private final int mask;

        public BitwiseXorFunc(int mask) {
            this.mask = mask;
        }

        @Override
        public Short apply(Short source) {
            return source != null ? (short) (source ^ mask) : null;
        }

    }

    static class EQFunc extends AbstractFuncWithHints<Short, Boolean> {

        private final short value;

        public EQFunc(short value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Short source) {
            return source != null && source == value;
        }

    }

    static class FromFunc extends AbstractFuncWithHints<Object, Short> {

        @Override
        public Short apply(Object source) {
            if (source instanceof Number) {
                return ((Number) source).shortValue();
            } else if (source instanceof String) {
                return Short.parseShort((String) source);
            } else {
                return null;
            }
        }

    }

    static class FromStringFunc extends AbstractFuncWithHints<String, Short> {

        @Override
        public Short apply(String source) {
            return source != null ? Short.parseShort(source) : null;
        }

    }

    static class FromNumberFunc extends AbstractFuncWithHints<Number, Short> {

        @Override
        public Short apply(Number source) {
            return source != null ? source.shortValue() : null;
        }

    }

    static abstract class FromList<S> extends AbstractFuncWithHints<List<S>, List<Short>> {

        @Override
        public final List<Short> apply(List<S> source) {
            if (source != null) {
                List<Short> values = new ArrayList<>(source.size());
                for (S e : source) {
                    values.add(toShort(e));
                }
                return values;
            }
            return null;
        }

        protected abstract Short toShort(S e);

    }

    static class FromNumberList<N extends Number> extends FromList<N> {

        @Override
        protected Short toShort(N s) {
            if (s != null) {
                return s.shortValue();
            } else {
                return null;
            }
        }

    }

    static class FromStringList extends FromList<String> {

        @Override
        protected Short toShort(String s) {
            if (s != null) {
                return Short.parseShort(s);
            } else {
                return null;
            }
        }

    }

    static class GEFunc extends AbstractFuncWithHints<Short, Boolean> {

        private final short value;

        public GEFunc(short value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Short source) {
            return source != null && source >= value;
        }

    }

    static class GTFunc extends AbstractFuncWithHints<Short, Boolean> {

        private final short value;

        public GTFunc(short value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Short source) {
            return source != null && source > value;
        }

    }

    static class LEFunc extends AbstractFuncWithHints<Short, Boolean> {

        private final short value;

        public LEFunc(short value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Short source) {
            return source != null && source <= value;
        }

    }

    static class LiteralFunc extends AbstractFuncWithHints<Object, Short> {

        private final short value;

        public LiteralFunc(short value) {
            this.value = value;
        }


        @Override
        public Short apply(Object source) {
            return value;
        }

    }

    static class LTFunc extends AbstractFuncWithHints<Short, Boolean> {

        private final short value;

        public LTFunc(short value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Short source) {
            return source != null && source < value;
        }

    }

    static class WithinFunc extends AbstractFuncWithHints<Short, Boolean> {

        private final short lowerBound;
        private final short upperBound;
        private final boolean lowerInclusive;
        private final boolean upperInclusive;

        public WithinFunc(short lowerBound, short upperBound, boolean lowerInclusive, boolean upperInclusive) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.lowerInclusive = lowerInclusive;
            this.upperInclusive = upperInclusive;
        }

        @Override
        public Boolean apply(Short value) {
            if (value != null) {
                return  ((lowerInclusive && value >= lowerBound) || (!lowerInclusive && value > lowerBound)) &&
                        ((upperInclusive && value <= upperBound) || (!upperInclusive && value < upperBound));
            } else {
                return Boolean.FALSE;
            }
        }

    }

    private Func createBitwiseAndFunc(TreeNode spec) {
        final short mask = Short.parseShort(spec.getDataOfChildAt(0));
        return new BitwiseAndFunc(mask);
    }

    private Func createBitwiseNotFunc(TreeNode spec) {
        return new BitwiseNotFunc();
    }

    private Func createBitwiseOrFunc(TreeNode spec) {
        final short mask = Short.parseShort(spec.getDataOfChildAt(0));
        return new BitwiseOrFunc(mask);
    }

    private Func createBitwiseXorFunc(TreeNode spec) {
        final short mask = Short.parseShort(spec.getDataOfChildAt(0));
        return new BitwiseXorFunc(mask);
    }

    private Func createFromFunc(TreeNode spec) {
        // SHORT:FROM(<sourceType>)
        final String sourceType = spec.getDataOfChildAt(0);

        // SHORT:FROM()
        if (sourceType == null) {
            return new FromFunc();
        }

        // SHORT:FROM(String)
        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringFunc();
        }

        // SHORT:FROM(Double|Float|Integer|Long|Short) or simply SHORT:FROM(Number)
        if (TypeHelper.isNumberType(sourceType) || "number".equalsIgnoreCase(sourceType)) {
            return new FromNumberFunc();
        }

        throw FuncHelper.createCreationException(
                spec,
                "SHORT:FROM(sourceType)",
                "SHORT:FROM(string)",
                new Exception(String.format("sourceType '%s' is not supported", sourceType))
        );
    }

    private Func createEQFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new EQFunc(Short.parseShort(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "SHORT:EQ(literal)",
                    "SHORT:EQ(10.0)"
            );
        }
    }

    private Func createGEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new GEFunc(Short.parseShort(v));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "SHORT:GE(value)",
                    "SHORT:GE(10)"
            );
        }
    }

    private Func createGTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new GTFunc(Short.parseShort(v));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "SHORT:GT(value)",
                    "SHORT:GT(10)"
            );
        }
    }

    private Func createLEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new LEFunc(Short.parseShort(v));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "SHORT:LE(value)",
                    "SHORT:LE(10)"
            );
        }
    }

    private Func createLTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new LTFunc(Short.parseShort(v));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "SHORT:LT(value)",
                    "SHORT:LT(10)"
            );
        }
    }

    private Func createLiteralFunc(TreeNode spec) {
        // SHORT(literal)
        final String literal = spec.getDataOfChildAt(0);
        try {
            return new LiteralFunc(Short.parseShort(literal));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "SHORT(value)",
                    "SHORT(10)"
            );
        }
    }

    private Func createListFromFunc(TreeNode spec) {
        String sourceElementType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceElementType)) {
            return new FromStringList();
        }

        if (TypeHelper.isNumberType(sourceElementType) || "number".equalsIgnoreCase(sourceElementType)) {
            return new FromNumberList();
        }

        throw FuncHelper.createCreationException(
                spec,
                "LIST<SHORT>:FROM(sourceType)",
                "LIST<SHORT>:FROM(string)",
                new Exception(String.format("Specified sourceType '%s' is not supported", sourceElementType))
        );
    }

    private Func createWithinFunc(TreeNode spec) {
        // SHORT:WITHIN(lowerBound,upperBound,I|E,I|E)

        final String l = spec.getDataOfChildAt(0);
        final String u = spec.getDataOfChildAt(1);
        final String li = spec.getDataOfChildAt(2, "I");
        final String ui = spec.getDataOfChildAt(3, "I");

        try {
            short lowerBound = Short.parseShort(l);
            short upperBound = Short.parseShort(u);

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
                    "SHORT:WITHIN(lowerBound,upperBound,I|E,I|E)",
                    "SHORT:WITHIN(1,100) or SHORT:WITHIN(1,100,E,E) or SHORT:WITHIN(1,100,I,E)"
            );
        }
    }

}
