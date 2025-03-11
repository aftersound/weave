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
public class IntegerFuncFactory extends MasterAwareFuncFactory {

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        switch (funcName) {
            case "INT":
            case "INTEGER": {
                return createLiteralFunc(spec);
            }
            case "INT:BAND":
            case "INTEGER:BAND": {
                return createBitwiseAndFunc(spec);
            }
            case "INT:BNOT":
            case "INTEGER:BNOT": {
                return createBitwiseNotFunc(spec);
            }
            case "INT:BOR":
            case "INTEGER:BOR": {
                return createBitwiseOrFunc(spec);
            }
            case "INT:BXOR":
            case "INTEGER:BXOR": {
                return createBitwiseXorFunc(spec);
            }
            case "INT:FROM":
            case "INTEGER:FROM": {
                return createFromFunc(spec);
            }
            case "INT:EQ":
            case "INTEGER:EQ": {
                return createEQFunc(spec);
            }
            case "INT:GE":
            case "INTEGER:GE": {
                return createGEFunc(spec);
            }
            case "INT:GT":
            case "INTEGER:GT": {
                return createGTFunc(spec);
            }
            case "INT:LE":
            case "INTEGER:LE": {
                return createLEFunc(spec);
            }
            case "INT:LT":
            case "INTEGER:LT": {
                return createLTFunc(spec);
            }
            case "LIST<INT>:FROM":
            case "LIST<INTEGER>:FROM": {
                return createListFromFunc(spec);
            }
            case "INT:WITHIN":
            case "INTEGER:WITHIN": {
                return createWithinFunc(spec);
            }
            default: {
                return null;
            }
        }

    }

    static class BitwiseAndFunc extends AbstractFuncWithHints<Integer, Integer> {

        private final int mask;

        public BitwiseAndFunc(int mask) {
            this.mask = mask;
        }

        @Override
        public Integer apply(Integer source) {
            return source != null ? (source & mask) : null;
        }

    }

    static class BitwiseNotFunc extends AbstractFuncWithHints<Integer, Integer> {

        @Override
        public Integer apply(Integer source) {
            return source != null ? (~source) : null;
        }

    }

    static class BitwiseOrFunc extends AbstractFuncWithHints<Integer, Integer> {

        private final int mask;

        public BitwiseOrFunc(int mask) {
            this.mask = mask;
        }

        @Override
        public Integer apply(Integer source) {
            return source != null ? (source | mask) : null;
        }

    }

    static class BitwiseXorFunc extends AbstractFuncWithHints<Integer, Integer> {

        private final int mask;

        public BitwiseXorFunc(int mask) {
            this.mask = mask;
        }

        @Override
        public Integer apply(Integer source) {
            return source != null ? (source ^ mask) : null;
        }

    }

    static class EQFunc extends AbstractFuncWithHints<Integer, Boolean> {

        private final int value;

        public EQFunc(int value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Integer source) {
            return source != null && source == value;
        }

    }

    static class FromFunc extends AbstractFuncWithHints<Object, Integer> {

        @Override
        public Integer apply(Object source) {
            if (source instanceof Number) {
                return ((Number) source).intValue();
            } else if (source instanceof String) {
                return Integer.parseInt((String) source);
            } else {
                return null;
            }
        }

    }

    static class FromStringFunc extends AbstractFuncWithHints<String, Integer> {

        @Override
        public Integer apply(String source) {
            return source != null ? Integer.parseInt(source) : null;
        }

    }

    static class FromNumberFunc extends AbstractFuncWithHints<Number, Integer> {

        @Override
        public Integer apply(Number source) {
            return source != null ? source.intValue() : null;
        }

    }

    static abstract class FromList<S> extends AbstractFuncWithHints<List<S>, List<Integer>> {

        @Override
        public final List<Integer> apply(List<S> source) {
            if (source != null) {
                List<Integer> values = new ArrayList<>(source.size());
                for (S e : source) {
                    values.add(toInteger(e));
                }
                return values;
            }
            return null;
        }

        protected abstract Integer toInteger(S e);

    }

    static class FromNumberList<N extends Number> extends FromList<N> {

        @Override
        protected Integer toInteger(N s) {
            if (s != null) {
                return s.intValue();
            } else {
                return null;
            }
        }

    }

    static class FromStringList extends FromList<String> {

        @Override
        protected Integer toInteger(String s) {
            if (s != null) {
                return Integer.parseInt(s);
            } else {
                return null;
            }
        }

    }

    static class GEFunc extends AbstractFuncWithHints<Integer, Boolean> {

        private final int value;

        public GEFunc(int value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Integer source) {
            return source != null && source >= value;
        }

    }

    static class GTFunc extends AbstractFuncWithHints<Integer, Boolean> {

        private final int value;

        public GTFunc(int value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Integer source) {
            return source != null && source > value;
        }

    }

    static class LEFunc extends AbstractFuncWithHints<Integer, Boolean> {

        private final int value;

        public LEFunc(int value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Integer source) {
            return source != null && source <= value;
        }

    }
    
    static class LiteralFunc extends AbstractFuncWithHints<String, Integer> {

        private final int value;

        public LiteralFunc(int value) {
            this.value = value;
        }


        @Override
        public Integer apply(String str) {
            return value;
        }
        
    }

    static class LTFunc extends AbstractFuncWithHints<Integer, Boolean> {

        private final int value;

        public LTFunc(int value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Integer source) {
            return source != null && source < value;
        }

    }

    static class WithinFunc extends AbstractFuncWithHints<Integer, Boolean> {

        private final int lowerBound;
        private final int upperBound;
        private final boolean lowerInclusive;
        private final boolean upperInclusive;

        public WithinFunc(int lowerBound, int upperBound, boolean lowerInclusive, boolean upperInclusive) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.lowerInclusive = lowerInclusive;
            this.upperInclusive = upperInclusive;
        }

        @Override
        public Boolean apply(Integer value) {
            if (value != null) {
                return  ((lowerInclusive && value >= lowerBound) || (!lowerInclusive && value > lowerBound)) &&
                        ((upperInclusive && value <= upperBound) || (!upperInclusive && value < upperBound));
            } else {
                return Boolean.FALSE;
            }
        }

    }

    private Func createBitwiseAndFunc(TreeNode spec) {
        final int mask = Integer.parseInt(spec.getDataOfChildAt(0));
        return new BitwiseAndFunc(mask);
    }

    private Func createBitwiseNotFunc(TreeNode spec) {
        return new BitwiseNotFunc();
    }

    private Func createBitwiseOrFunc(TreeNode spec) {
        final int mask = Integer.parseInt(spec.getDataOfChildAt(0));
        return new BitwiseOrFunc(mask);
    }

    private Func createBitwiseXorFunc(TreeNode spec) {
        final int mask = Integer.parseInt(spec.getDataOfChildAt(0));
        return new BitwiseXorFunc(mask);
    }

    private Func createFromFunc(TreeNode spec) {
        // INTEGER:FROM(<sourceType>)
        final String sourceType = spec.getDataOfChildAt(0);

        // INTEGER:FROM()
        if (sourceType == null) {
            return new FromFunc();
        }

        // INTEGER:FROM(String)
        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringFunc();
        }

        // INTEGER:FROM(Double|Float|Integer|Integer|Short) or simply Integer:FROM(Number)
        if (TypeHelper.isNumberType(sourceType) || "number".equalsIgnoreCase(sourceType)) {
            return new FromNumberFunc();
        }

        throw FuncHelper.createCreationException(
                spec,
                "INTEGER:FROM(sourceType)",
                "INTEGER:FROM(string)",
                new Exception(String.format("sourceType '%s' is not supported", sourceType))
        );
    }

    private Func createEQFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new EQFunc(Integer.parseInt(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "INTEGER:EQ(literal)",
                    "INTEGER:EQ(10.0)"
            );
        }
    }

    private Func createGEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            int value = Integer.parseInt(v);
            return new GEFunc(value);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "INTEGER:GE(value)",
                    "INTEGER:GE(10)"
            );
        }
    }

    private Func createGTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            int value = Integer.parseInt(v);
            return new GTFunc(value);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "INTEGER:GT(value)",
                    "INTEGER:GT(10)"
            );
        }
    }

    private Func createLEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            int value = Integer.parseInt(v);
            return new LEFunc(value);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "INTEGER:LE(value)",
                    "INTEGER:LE(10)"
            );
        }
    }

    private Func createLTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            int value = Integer.parseInt(v);
            return new LTFunc(value);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "INTEGER:LT(value)",
                    "INTEGER:LT(10)"
            );
        }
    }

    private Func createLiteralFunc(TreeNode spec) {
        // INT(literal)
        final String literal = spec.getDataOfChildAt(0);
        try {
            int value = Integer.parseInt(literal);
            return new LiteralFunc(value);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "INTEGER(value)",
                    "INTEGER(10)"
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
                "LIST<INTEGER>:FROM(sourceType)",
                "LIST<INTEGER>:FROM(string)",
                new Exception(String.format("Specified sourceType '%s' is not supported", sourceElementType))
        );
    }

    private Func createWithinFunc(TreeNode spec) {
        // INTEGER:WITHIN(lowerBound,upperBound,I|E,I|E)

        final String l = spec.getDataOfChildAt(0);
        final String u = spec.getDataOfChildAt(1);
        final String li = spec.getDataOfChildAt(2, "I");
        final String ui = spec.getDataOfChildAt(3, "I");

        try {
            int lowerBound = Integer.parseInt(l);
            int upperBound = Integer.parseInt(u);

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
                    "INTEGER:WITHIN(lowerBound,upperBound,I|E,I|E)",
                    "INTEGER:WITHIN(1,100) or INTEGER:WITHIN(1,100,E,E) or INTEGER:WITHIN(1,100,I,E)"
            );
        }
    }

}
