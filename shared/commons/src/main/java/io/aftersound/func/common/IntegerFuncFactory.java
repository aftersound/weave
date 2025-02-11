package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.TypeHelper;
import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.aftersound.func.FuncHelper.createCreationException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class IntegerFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor.builder("INTEGER", "TBD", "TBD")
//                    .withAliases("INT")
//                    .build(),
//            Descriptor.builder("INTEGER:BAND", "TBD", "TBD")
//                    .withAliases("INT:BAND")
//                    .build(),
//            Descriptor.builder("INTEGER:BNOT", "TBD", "TBD")
//                    .withAliases("INT:BNOT")
//                    .build(),
//            Descriptor.builder("INTEGER:BOR", "TBD", "TBD")
//                    .withAliases("INT:BOR")
//                    .build(),
//            Descriptor.builder("INTEGER:BNOT", "TBD", "TBD")
//                    .withAliases("INT:BNOT")
//                    .build(),
//            Descriptor.builder("INTEGER:BXOR", "TBD", "TBD")
//                    .withAliases("INT:BXOR")
//                    .build(),
//            Descriptor.builder("INTEGER:BNOT", "TBD", "TBD")
//                    .withAliases("INT:BNOT")
//                    .build(),
//            Descriptor.builder("INTEGER:FROM", "TBD", "TBD")
//                    .withAliases("INT:FROM")
//                    .build(),
//            Descriptor.builder("INTEGER:LIST:FROM", "TBD", "TBD")
//                    .withAliases("INT:LIST:FROM")
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
            case "INT:LIST:FROM":
            case "INTEGER:LIST:FROM": {
                return createListFromFunc(spec);
            }
            case "LT:WITHIN":
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
        public Boolean apply(Integer integer) {
            return integer != null && integer >= value;
        }

    }

    static class GTFunc extends AbstractFuncWithHints<Integer, Boolean> {

        private final int value;

        public GTFunc(int value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Integer integer) {
            return integer != null && integer > value;
        }

    }

    static class LEFunc extends AbstractFuncWithHints<Integer, Boolean> {

        private final int value;

        public LEFunc(int value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Integer integer) {
            return integer != null && integer <= value;
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
        public Boolean apply(Integer integer) {
            return integer != null && integer < value;
        }

    }

    static class WithinFunc extends AbstractFuncWithHints<Integer, Boolean> {

        private final int lowerBound;
        private final int upperBound;

        public WithinFunc(int lowerBound, int upperBound) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
        }

        @Override
        public Boolean apply(Integer integer) {
            return integer != null && integer >= lowerBound && integer <= upperBound;
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

        // INTEGER:FROM(String)
        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringFunc();
        }

        // INTEGER:FROM(Double|Float|Integer|Integer|Short) or simply Integer:FROM(Number)
        if (TypeHelper.isNumberType(sourceType)) {
            return new FromNumberFunc();
        }

        throw new CreationException(sourceType + " specified in value function spec as source type is not supported");
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
        String sourceType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringList();
        }

        if (TypeHelper.isNumberType(sourceType)) {
            return new FromNumberList();
        }

        throw new CreationException(sourceType + " specified in value function spec as source type is not supported");
    }

    private Func createWithinFunc(TreeNode spec) {
        final String l = spec.getDataOfChildAt(0);
        final String u = spec.getDataOfChildAt(1);

        try {
            int lowerBound = Integer.parseInt(l);
            int upperBound = Integer.parseInt(u);
            return new WithinFunc(lowerBound, upperBound);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "INTEGER:WITHIN(lowerBound, upperBound)",
                    "INTEGER:WITHIN(1,100)"
            );
        }
    }

}
