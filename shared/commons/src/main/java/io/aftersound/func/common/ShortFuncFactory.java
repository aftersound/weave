package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.TypeHelper;
import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ShortFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor.builder("SHORT", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("SHORT:FROM", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("SHORT:LIST:FROM", "TBD", "TBD")
//                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("SHORT".equals(funcName)) {
            return createLiteralFunc(spec);
        }

        if ("SHORT:FROM".equals(funcName)) {
            return createFromFunc(spec);
        }

        if ("SHORT:LIST:FROM".equals(funcName)) {
            return createListFromFunc(spec);
        }

        return null;
    }

    static class FromStringFunc extends AbstractFuncWithHints<String, Short> {

        public static final FromStringFunc INSTANCE = new FromStringFunc();

        private FromStringFunc() {
        }

        @Override
        public Short apply(String source) {
            return source != null ? Short.parseShort(source) : null;
        }

    }

    static class FromNumberFunc extends AbstractFuncWithHints<Number, Short> {

        public static final FromNumberFunc INSTANCE = new FromNumberFunc();

        private FromNumberFunc() {
        }

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

    private Func createFromFunc(TreeNode spec) {
        // SHORT:FROM(<sourceType>)
        final String sourceType = spec.getDataOfChildAt(0);

        // SHORT:FROM(String)
        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return FromStringFunc.INSTANCE;
        }

        // SHORT:FROM(Double|Float|Short|Short|Short) or simply Short:FROM(Number)
        if (TypeHelper.isNumberType(sourceType)) {
            return FromNumberFunc.INSTANCE;
        }

        throw new CreationException(sourceType + " specified in value function spec as source type is not supported");
    }

    private Func createLiteralFunc(TreeNode spec) {
        // SHORT(literal)
        final String literal = spec.getDataOfChildAt(0);
        return masterFuncFactory.create(String.format("VAL(Short,%s)", literal));
    }

    private Func createListFromFunc(TreeNode spec) {
        // TO_SHORT_LIST(elementType)

        String sourceType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringList();
        }

        if (TypeHelper.isNumberType(sourceType)) {
            return new FromNumberList();
        }

        throw new CreationException(sourceType + " specified in value function spec as source type is not supported");
    }

}
