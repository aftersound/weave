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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DoubleFuncFactory extends MasterAwareFuncFactory {

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("DOUBLE".equals(funcName)) {
            return createLiteralFunc(spec);
        }

        if ("DOUBLE:EQ".equals(funcName)) {
            return createEQFunc(spec);
        }

        if ("DOUBLE:FROM".equals(funcName)) {
            return createFromFunc(spec);
        }

        if ("DOUBLE:GE".equals(funcName)) {
            return createGEFunc(spec);
        }

        if ("DOUBLE:GT".equals(funcName)) {
            return createGTFunc(spec);
        }

        if ("DOUBLE:LE".equals(funcName)) {
            return createLEFunc(spec);
        }

        if ("DOUBLE:LT".equals(funcName)) {
            return createLTFunc(spec);
        }

        if ("DOUBLE:WITHIN".equals(funcName)) {
            return createWithinFunc(spec);
        }

        if ("LIST<DOUBLE>:FROM".equals(funcName)) {
            return createListFromFunc(spec);
        }

        return null;
    }

    static class LiteralFunc extends AbstractFuncWithHints<Object, Double> {

        private final Double value;

        public LiteralFunc(Double value) {
            this.value = value;
        }

        @Override
        public Double apply(Object s) {
            return value;
        }

    }

    static class EQFunc extends AbstractFuncWithHints<Double, Boolean> {

        private final double value;

        public EQFunc(double value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Double d) {
            return d != null && d == value;
        }

    }

    static class FromNumberFunc<N extends Number> extends AbstractFuncWithHints<N, Double> {

        @Override
        public Double apply(N source) {
            if (source != null) {
                return source.doubleValue();
            } else {
                return null;
            }
        }

    }

    static class FromStringFunc extends AbstractFuncWithHints<String, Double> {

        @Override
        public Double apply(String source) {
            if (source != null) {
                return Double.parseDouble(source);
            } else {
                return null;
            }
        }
    }

    static class GEFunc extends AbstractFuncWithHints<Double, Boolean> {

        private final double value;

        public GEFunc(double value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Double d) {
            return d != null && d >= value;
        }

    }

    static class GTFunc extends AbstractFuncWithHints<Double, Boolean> {

        private final double value;

        public GTFunc(double value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Double d) {
            return d != null && d > value;
        }

    }

    static class LEFunc extends AbstractFuncWithHints<Double, Boolean> {

        private final double value;

        public LEFunc(double value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Double d) {
            return d != null && d <= value;
        }

    }

    static class LTFunc extends AbstractFuncWithHints<Double, Boolean> {

        private final double value;

        public LTFunc(double value) {
            this.value = value;
        }

        @Override
        public Boolean apply(Double d) {
            return d != null && d < value;
        }

    }


    static class WithinFunc extends AbstractFuncWithHints<Double, Boolean> {

        private final double lowerBound;
        private final double upperBound;
        private final boolean lowerInclusive;
        private final boolean upperInclusive;

        public WithinFunc(double lowerBound, double upperBound, boolean lowerInclusive, boolean upperInclusive) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.lowerInclusive = lowerInclusive;
            this.upperInclusive = upperInclusive;
        }

        @Override
        public Boolean apply(Double value) {
            if (value != null) {
                return  ((lowerInclusive && value >= lowerBound) || (!lowerInclusive && value > lowerBound)) &&
                        ((upperInclusive && value <= upperBound) || (!upperInclusive && value < upperBound));
            } else {
                return Boolean.FALSE;
            }
        }
    }

    static abstract class FromList<S> extends AbstractFuncWithHints<List<S>, List<Double>> {

        @Override
        public final List<Double> apply(List<S> source) {
            if (source != null) {
                List<Double> values = new ArrayList<>(source.size());
                for (S e : source) {
                    values.add(toDouble(e));
                }
                return values;
            }
            return null;
        }

        protected abstract Double toDouble(S e);

    }

    static class FromNumberList<N extends Number> extends FromList<N> {

        @Override
        protected Double toDouble(N s) {
            if (s != null) {
                return s.doubleValue();
            } else {
                return null;
            }
        }

    }

    static class FromStringList extends FromList<String> {

        @Override
        protected Double toDouble(String s) {
            if (s != null) {
                return Double.parseDouble(s);
            } else {
                return null;
            }
        }

    }

    private Func createEQFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new EQFunc(Double.parseDouble(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "DOUBLE:EQ(literal)",
                    "DOUBLE:EQ(10.0)"
            );
        }
    }

    private Func createGEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new GEFunc(Double.parseDouble(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "DOUBLE:GE(literal)",
                    "DOUBLE:GE(10.0)"
            );
        }
    }

    private Func createGTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new GTFunc(Double.parseDouble(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "DOUBLE:GT(literal)",
                    "DOUBLE:GT(10.0)"
            );
        }
    }

    private Func createLEFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new LEFunc(Double.parseDouble(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "DOUBLE:LE(literal)",
                    "DOUBLE:LE(10.0)"
            );
        }
    }

    private Func createLTFunc(TreeNode spec) {
        final String v = spec.getDataOfChildAt(0);
        try {
            return new LTFunc(Double.parseDouble(v));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "DOUBLE:LT(literal)",
                    "DOUBLE:LT(10.0)"
            );
        }
    }

    private Func createFromFunc(TreeNode treeNode) {
        // DOUBLE:FROM(<sourceType>)
        final String sourceType = treeNode.getDataOfChildAt(0);

        // DOUBLE:FROM(String)
        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringFunc();
        }

        // DOUBLE:FROM(Double|Float|Integer|Long|Short)
        if (TypeHelper.isNumberType(sourceType) || "number".equalsIgnoreCase(sourceType)) {
            return new FromNumberFunc();
        }

        throw FuncHelper.createCreationException(
                treeNode,
                "DOUBLE:FROM(String|Double|Float|Integer|Short|Number)",
                "DOUBLE(String)"
        );
    }

    private Func createLiteralFunc(TreeNode spec) {
        // DOUBLE(literal)
        final String literal = spec.getDataOfChildAt(0);

        try {
            return new LiteralFunc(Double.parseDouble(literal));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "DOUBLE(literal)",
                    "DOUBLE(10.0)"
            );
        }
    }

    private Func createWithinFunc(TreeNode spec) {
        // DOUBLE:WITHIN(lowerBound,upperBound,I|E,I|E)

        final String l = spec.getDataOfChildAt(0);
        final String u = spec.getDataOfChildAt(1);
        final String li = spec.getDataOfChildAt(2, "I");
        final String ui = spec.getDataOfChildAt(3, "I");

        try {
            double lowerBound = Double.parseDouble(l);
            double upperBound = Double.parseDouble(u);

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
                    "DOUBLE:WITHIN(lowerBound,upperBound,I|E,I|E)",
                    "DOUBLE:WITHIN(1,100) or DOUBLE:WITHIN(1,100,E,E) or DOUBLE:WITHIN(1,100,I,E)"
            );
        }
    }

    private Func createListFromFunc(TreeNode spec) {
        // LIST<DOUBLE>:FROM(sourceElementType)
        String sourceElementType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceElementType)) {
            return new FromStringList();
        }

        if (TypeHelper.isNumberType(sourceElementType) || "number".equalsIgnoreCase(sourceElementType)) {
            return new FromNumberList();
        }

        throw FuncHelper.createCreationException(
                spec,
                "LIST<DOUBLE>:FROM(sourceElementType)",
                "LIST<DOUBLE>:FROM(string)",
                new Exception(String.format("Specified sourceElementType '%s' is not supported", sourceElementType))
        );
    }

}
