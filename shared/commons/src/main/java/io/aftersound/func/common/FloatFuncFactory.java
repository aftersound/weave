package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.TypeHelper;
import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FloatFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor.builder("FLOAT", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("FLOAT:FROM", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("FLOAT:LIST:FROM", "TBD", "TBD")
//                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("FLOAT".equals(funcName)) {
            return createLiteralFunc(spec);
        }

        if ("FLOAT:FROM".equals(funcName)) {
            return createFromFunc(spec);
        }

        if ("LIST<FLOAT>:FROM".equals(funcName)) {
            return createListFromFunc(spec);
        }

        return null;
    }

    static class LiteralFunc extends AbstractFuncWithHints<Object, Float> {

        private final Float value;

        public LiteralFunc(Float value) {
            this.value = value;
        }

        @Override
        public Float apply(Object s) {
            return value;
        }

    }

    static class FromNumberFunc<N extends Number> extends AbstractFuncWithHints<N, Float> {

        @Override
        public Float apply(N source) {
            if (source != null) {
                return source.floatValue();
            } else {
                return null;
            }
        }

    }

    static class FromStringFunc extends AbstractFuncWithHints<String, Float> {

        @Override
        public Float apply(String source) {
            if (source != null) {
                return Float.parseFloat(source);
            } else {
                return null;
            }
        }
    }

    static abstract class FromList<S> extends AbstractFuncWithHints<List<S>, List<Float>> {

        @Override
        public final List<Float> apply(List<S> source) {
            if (source != null) {
                List<Float> values = new ArrayList<>(source.size());
                for (S e : source) {
                    values.add(toFloat(e));
                }
                return values;
            }
            return null;
        }

        protected abstract Float toFloat(S e);

    }

    static class FromNumberList<N extends Number> extends FromList<N> {

        @Override
        protected Float toFloat(N s) {
            if (s != null) {
                return s.floatValue();
            } else {
                return null;
            }
        }

    }

    static class FromStringList extends FromList<String> {

        @Override
        protected Float toFloat(String s) {
            if (s != null) {
                return Float.parseFloat(s);
            } else {
                return null;
            }
        }

    }

    private Func createFromFunc(TreeNode treeNode) {
        // FLOAT:FROM(<sourceType>)
        final String sourceType = treeNode.getDataOfChildAt(0);

        // FLOAT:FROM(String)
        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringFunc();
        }

        // FLOAT:FROM(Double|Float|Integer|Long|Short|Number)
        if (TypeHelper.isNumberType(sourceType)) {
            return new FromNumberFunc();
        }

        throw FuncHelper.createCreationException(
                treeNode,
                "FLOAT:FROM(sourceType)",
                "FLOAT:FROM(string)",
                new Exception(String.format("sourceType '%s' is not supported", sourceType))
        );
    }

    private Func createLiteralFunc(TreeNode spec) {
        // FLOAT(literal)
        final String literal = spec.getDataOfChildAt(0);
        try {
            return new LiteralFunc(Float.parseFloat(literal));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "FLOAT(literal)",
                    "FLOAT(100.00)",
                    e
            );
        }
    }

    private Func createListFromFunc(TreeNode spec) {
        // LIST<FLOAT>:FROM(elementType)

        String sourceType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringList();
        }

        if (TypeHelper.isNumberType(sourceType)) {
            return new FromNumberList();
        }

        throw FuncHelper.createCreationException(
                spec,
                "LIST<FLOAT>:FROM(sourceType)",
                "LIST<FLOAT>:FROM(string)",
                new Exception(String.format("Specified sourceType '%s' is not supported", sourceType))
        );
    }

}
