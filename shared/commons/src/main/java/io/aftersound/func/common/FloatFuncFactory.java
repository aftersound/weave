package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.TypeHelper;
import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FloatFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = DescriptorHelper.getDescriptors(FloatFuncFactory.class);

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

        if ("FLOAT:EQ".equals(funcName)) {
            return createEQFunc(spec);
        }

        if ("FLOAT:FROM".equals(funcName)) {
            return createFromFunc(spec);
        }

        if ("FLOAT:GE".equals(funcName)) {
            return createGEFunc(spec);
        }

        if ("FLOAT:GT".equals(funcName)) {
            return createGTFunc(spec);
        }

        if ("FLOAT:LE".equals(funcName)) {
            return createLEFunc(spec);
        }

        if ("FLOAT:LT".equals(funcName)) {
            return createLTFunc(spec);
        }

        if ("FLOAT:WITHIN".equals(funcName)) {
            return createWithinFunc(spec);
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

    static class EQFunc extends AbstractFuncWithHints<Float, Boolean> {

        private final float value;

        public EQFunc(float value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Float v) {
            return v != null && v == value;
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

    static class GEFunc extends AbstractFuncWithHints<Float, Boolean> {

        private final float value;

        public GEFunc(float value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Float v) {
            return v != null && v >= value;
        }

    }

    static class GTFunc extends AbstractFuncWithHints<Float, Boolean> {

        private final float value;

        public GTFunc(float value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Float v) {
            return v != null && v > value;
        }

    }

    static class LEFunc extends AbstractFuncWithHints<Float, Boolean> {

        private final float value;

        public LEFunc(float value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Float v) {
            return v != null && v <= value;
        }

    }

    static class LTFunc extends AbstractFuncWithHints<Float, Boolean> {

        private final float value;

        public LTFunc(float value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Float v) {
            return v != null && v < value;
        }

    }


    static class WithinFunc extends AbstractFuncWithHints<Float, Boolean> {

        private final float lowerBound;
        private final float upperBound;
        private final boolean lowerInclusive;
        private final boolean upperInclusive;

        public WithinFunc(float lowerBound, float upperBound, boolean lowerInclusive, boolean upperInclusive) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.lowerInclusive = lowerInclusive;
            this.upperInclusive = upperInclusive;
        }

        @Override
        public Boolean apply(Float value) {
            if (value != null) {
                return  ((lowerInclusive && value >= lowerBound) || (!lowerInclusive && value > lowerBound)) &&
                        ((upperInclusive && value <= upperBound) || (!upperInclusive && value < upperBound));
            } else {
                return Boolean.FALSE;
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

    private Func createEQFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new EQFunc(Float.parseFloat(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "FLOAT:EQ(literal)",
                    "FLOAT:EQ(10.0)"
            );
        }
    }

    private Func createGEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new GEFunc(Float.parseFloat(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "FLOAT:GE(literal)",
                    "FLOAT:GE(10.0)"
            );
        }
    }

    private Func createGTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new GTFunc(Float.parseFloat(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "FLOAT:GT(literal)",
                    "FLOAT:GT(10.0)"
            );
        }
    }

    private Func createLEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new LEFunc(Float.parseFloat(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "FLOAT:LE(literal)",
                    "FLOAT:LE(10.0)"
            );
        }
    }

    private Func createLTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new LTFunc(Float.parseFloat(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "FLOAT:LT(literal)",
                    "FLOAT:LT(10.0)"
            );
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
        if (TypeHelper.isNumberType(sourceType) || "number".equalsIgnoreCase(sourceType)) {
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
        // LIST<FLOAT>:FROM(sourceElementType)

        String sourceElementType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceElementType)) {
            return new FromStringList();
        }

        if (TypeHelper.isNumberType(sourceElementType) || "number".equalsIgnoreCase(sourceElementType)) {
            return new FromNumberList();
        }

        throw FuncHelper.createCreationException(
                spec,
                "LIST<FLOAT>:FROM(sourceElementType)",
                "LIST<FLOAT>:FROM(string)",
                new Exception(String.format("Specified sourceElementType '%s' is not supported", sourceElementType))
        );
    }

    private Func createWithinFunc(TreeNode spec) {
        // FLOAT:WITHIN(lowerBound,upperBound,I|E,I|E)

        final String l = spec.getDataOfChildAt(0);
        final String u = spec.getDataOfChildAt(1);
        final String li = spec.getDataOfChildAt(2, "I");
        final String ui = spec.getDataOfChildAt(3, "I");

        try {
            float lowerBound = Float.parseFloat(l);
            float upperBound = Float.parseFloat(u);

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
                    "FLOAT:WITHIN(lowerBound,upperBound,I|E,I|E)",
                    "FLOAT:WITHIN(1,100) or FLOAT:WITHIN(1,100,E,E) or FLOAT:WITHIN(1,100,I,E)"
            );
        }
    }
    
}
