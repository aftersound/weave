package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.schema.TypeHelper;
import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class DoubleFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor
//                    .builder(
//                            "DOUBLE",
//                            "Double literal",
//                            "Double"
//                    )
//                    .withDescription("Create a Double object with given literal")
//                    .withControls(
//                            Control
//                                    .builder(
//                                            "String",
//                                            "Source type"
//                                    )
//                                    .withAcceptedValues("Double", "Float", "Integer", "Long", "Short", "String")
//                                    .build()
//                    )
//                    .withExamples(
//                            Example.as(
//                                    "DOUBLE(888)",
//                                    "Create a Double object with value 888"
//                            )
//                    )
//                    .build(),
//            Descriptor
//                    .builder(
//                            "DOUBLE:FROM",
//                            "As specified by source type control parameter ",
//                            "Double"
//                    )
//                    .withDescription("Convert input value of specified source type into Double object")
//                    .withExamples(
//                            Example.as(
//                                    "DOUBLE:FROM(String)",
//                                    "Convert input string value into Double object"
//                            ),
//                            Example.as(
//                                    "DOUBLE:FROM(Float)",
//                                    "Convert input Float value into Double object"
//                            ),
//                            Example.as(
//                                    "DOUBLE:FROM(Integer)",
//                                    "Convert input Integer value into Double object"
//                            )
//                    )
//                    .build(),
//            Descriptor
//                    .builder(
//                            "DOUBLE:LIST:FROM",
//                            "list of values in source element type specified in control parameters",
//                            "list of Double"
//                    )
//                    .withDescription("Convert input list of values into list of Double(s)")
//                    .withControls(
//                            Control
//                                    .builder(
//                                            "String",
//                                            "Source element type"
//                                    )
//                                    .withAcceptedValues("Double", "Float", "Integer", "Long", "Short", "String")
//                                    .build()
//                    )
//                    .withExamples(
//                            Example.as(
//                                    "DOUBLE:LIST:FROM(Float)",
//                                    "Convert input list of Float(s) into list of Double(s)"
//                            ),
//                            Example.as(
//                                    "DOUBLE:LIST:FROM(String)",
//                                    "Convert input list of String values into list of Double(s)"
//                            )
//                    )
//                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("DOUBLE".equals(funcName)) {
            return createLiteralFunc(spec);
        }

        if ("DOUBLE:FROM".equals(funcName)) {
            return createFromFunc(spec);
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

    private Func createFromFunc(TreeNode treeNode) {
        // DOUBLE:FROM(<sourceType>)
        final String sourceType = treeNode.getDataOfChildAt(0);

        // DOUBLE:FROM(String)
        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringFunc();
        }

        // DOUBLE:FROM(Double|Float|Integer|Long|Short)
        if (TypeHelper.isNumberType(sourceType) || "number".equals(sourceType)) {
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

    private Func createListFromFunc(TreeNode spec) {
        // LIST<DOUBLE>:FROM(elementType)
        String sourceType = spec.getDataOfChildAt(0);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromStringList();
        }

        if (TypeHelper.isNumberType(sourceType)) {
            return new FromNumberList();
        }

        throw FuncHelper.createCreationException(
                spec,
                "LIST<DOUBLE>:FROM(sourceType)",
                "LIST<DOUBLE>:FROM(string)",
                new Exception(String.format("Specified sourceType '%s' is not supported", sourceType))
        );
    }

}
