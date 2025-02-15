package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.TypeHelper;
import io.aftersound.util.TreeNode;

import java.util.*;

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

        if ("LONG".equals(funcName)) {
            return createLiteralFunc(spec);
        }

        if ("LONG:BAND".equals(funcName)) {
            return createBitwiseAndFunc(spec);
        }

        if ("LONG:BNOT".equals(funcName)) {
            return createBitwiseNotFunc(spec);
        }

        if ("LONG:BOR".equals(funcName)) {
            return createBitwiseOrFunc(spec);
        }

        if ("LONG:BXOR".equals(funcName)) {
            return createBitwiseXorFunc(spec);
        }

        if ("LONG:FROM".equals(funcName)) {
            return createFromFunc(spec);
        }

        if ("LIST<LONG>:FROM".equals(funcName)) {
            return createListFrom(spec);
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

        // LONG:FROM(Date)
        if (ProtoTypes.DATE.matchIgnoreCase(sourceType)) {
            return new FromDateFunc();
        }

        // LONG:FROM(String)
        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringFunc();
        }

        // LONG:FROM(Double|Float|Integer|Long|Short) or simply LONG:FROM(Number)
        if (TypeHelper.isNumberType(sourceType)) {
            return new FromNumberFunc();
        }

        throw new CreationException(sourceType + " specified in value function spec as source type is not supported");
    }

    private Func createLiteralFunc(TreeNode spec) {
        // LONG(literal)
        final String literal = spec.getDataOfChildAt(0);
        return masterFuncFactory.create(String.format("VAL(Long,%s)", literal));
    }

    private Func createListFrom(TreeNode spec) {
        String sourceType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringList();
        }

        if (TypeHelper.isNumberType(sourceType)) {
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
