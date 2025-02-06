package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        if ("INT".equals(funcName)) {
            return createLiteralFunc(spec);
        }

        if ("INT:BAND".equals(funcName)) {
            return createBitwiseAndFunc(spec);
        }

        if ("INT:BNOT".equals(funcName)) {
            return createBitwiseNotFunc(spec);
        }

        if ("INT:BOR".equals(funcName)) {
            return createBitwiseOrFunc(spec);
        }

        if ("INT:BXOR".equals(funcName)) {
            return createBitwiseXorFunc(spec);
        }

        if ("INT:FROM".equals(funcName)) {
            return createFromFunc(spec);
        }

        if ("INT:LIST:FROM".equals(funcName)) {
            return createListFrom(spec);
        }

        if ("INTEGER".equals(funcName)) {
            return createLiteralFunc(spec);
        }

        if ("INTEGER:BAND".equals(funcName)) {
            return createBitwiseAndFunc(spec);
        }

        if ("INTEGER:BNOT".equals(funcName)) {
            return createBitwiseNotFunc(spec);
        }

        if ("INTEGER:BOR".equals(funcName)) {
            return createBitwiseOrFunc(spec);
        }

        if ("INTEGER:BXOR".equals(funcName)) {
            return createBitwiseXorFunc(spec);
        }

        if ("INTEGER:FROM".equals(funcName)) {
            return createFromFunc(spec);
        }

        if ("INTEGER:LIST:FROM".equals(funcName)) {
            return createListFrom(spec);
        }

        return null;
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
        if (ProtoTypes.isNumberType(sourceType)) {
            return new FromNumberFunc();
        }

        throw new CreationException(sourceType + " specified in value function spec as source type is not supported");
    }

    private Func createLiteralFunc(TreeNode spec) {
        // INT(literal)
        final String literal = spec.getDataOfChildAt(0);
        return masterFuncFactory.create(String.format("VAL(Integer,%s)", literal));
    }

    private Func createListFrom(TreeNode spec) {
        String sourceType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringList();
        }

        if (ProtoTypes.isNumberType(sourceType)) {
            return new FromNumberList();
        }

        throw new CreationException(sourceType + " specified in value function spec as source type is not supported");
    }

}
