package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.util.TreeNode;

import java.util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BooleanFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor
//                    .builder(
//                            "AND",
//                            "TBD",
//                            "TBD"
//                    )
//                    .build(),
//            Descriptor
//                    .builder(
//                            "BOOL",
//                            null,
//                            "Boolean"
//                    )
//                    .withDescription("Create Boolean object from literal")
//                    .withControls(
//                            Control
//                                    .builder(
//                                            "String",
//                                            "Boolean literal"
//                                    )
//                                    .withAcceptedValues("true", "false")
//                                    .build()
//                    )
//                    .withExamples(
//                            Example.as(
//                                    "BOOL(true)",
//                                    "Create Boolean.TRUE"
//                            )
//                    )
//                    .build(),
//            Descriptor.builder("BOOL:FROM", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("BOOL:LIST:FROM", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("BOOL:SET:FROM", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("EQ", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("NE", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("NOT", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("OR", "TBD", "TBD")
//                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("AND".equals(funcName)) {
            return createAndFunc(spec);
        }

        if ("BOOL".equals(funcName)) {
            return createLiteralFunc(spec);
        }

        if ("BOOL:FROM".equals(funcName)) {
            return createFromFunc(spec);
        }

        if ("BOOL:LIST:FROM".equals(funcName)) {
            return createListFromFunc(spec);
        }

        if ("BOOL:SET:FROM".equals(funcName)) {
            return createSetFromFunc(spec);
        }

        if ("EQ".equals(funcName)) {
            return createEqualFunc(spec);
        }

        if ("NE".equals(funcName)) {
            return createNotEqualFunc(spec);
        }

        if ("NOT".equals(funcName)) {
            return createNotFunc(spec);
        }

        if ("OR".equals(funcName)) {
            return createOrFunc(spec);
        }

        return null;
    }

    static class FromFunc<S> extends AbstractFuncWithHints<S,Boolean> {

        private final Class<S> sourceType;
        private final S valueTrue;
        private final S valueFalse;

        FromFunc(Class<S> sourceType, S valueTrue, S valueFalse) {
            this.sourceType = sourceType;
            this.valueTrue = valueTrue;
            this.valueFalse = valueFalse;
        }

        @Override
        public Boolean apply(S source) {
            return valueTrue.equals(source);
        }

    }

    static class AndFunc extends AbstractFuncWithHints<Object, Boolean> {

        private final List<Func<Object, Boolean>> operands;

        public AndFunc(List<Func<Object, Boolean>> operands) {
            this.operands = operands;
        }

        @Override
        public Boolean apply(Object source) {
            Boolean r = Boolean.TRUE;
            for (Func<Object, Boolean> valueFunc : operands) {
                Boolean b = valueFunc.apply(source);
                if (b == null || !b) {
                    r = Boolean.FALSE;
                    break;
                }
            }
            return r;
        }

    }

    static class OrFunc extends AbstractFuncWithHints<Object, Boolean> {

        private final List<Func<Object, Boolean>> operands;

        public OrFunc(List<Func<Object, Boolean>> operands) {
            this.operands = operands;
        }

        @Override
        public Boolean apply(Object source) {
            Boolean r = Boolean.FALSE;
            for (Func<Object, Boolean> valueFunc : operands) {
                Boolean b = valueFunc.apply(source);
                if (b != null && b) {
                    r = Boolean.TRUE;
                    break;
                }
            }
            return r;
        }

    }

    static class NotFunc extends AbstractFuncWithHints<Object, Boolean> {

        private final Func<Object, Boolean> operand;

        public NotFunc(Func<Object, Boolean> operand) {
            this.operand = operand;
        }

        @Override
        public Boolean apply(Object source) {
            Boolean b = operand.apply(source);
            return b != null && b ? Boolean.FALSE : Boolean.TRUE;
        }

    }

    static class EqualFunc extends AbstractFuncWithHints<Object, Boolean> {

        private final Func<Object, Object> operand1;
        private final Func<Object, Object> operand2;

        public EqualFunc(Func<Object, Object> operand1, Func<Object, Object> operand2) {
            this.operand1 = operand1;
            this.operand2 = operand2;
        }

        @Override
        public Boolean apply(Object source) {
            Object v1 = operand1.apply(source);
            Object v2 = operand2.apply(source);
            if (v1 != null) {
                return v1.equals(v2);
            } else {
                return v2 == null;
            }
        }

    }

    static class NotEqualFunc extends AbstractFuncWithHints<Object, Boolean> {

        private final Func<Object, Object> operand1;
        private final Func<Object, Object> operand2;

        public NotEqualFunc(Func<Object, Object> operand1, Func<Object, Object> operand2) {
            this.operand1 = operand1;
            this.operand2 = operand2;
        }

        @Override
        public Boolean apply(Object source) {
            Object v1 = operand1.apply(source);
            Object v2 = operand2.apply(source);
            if (v1 != null) {
                return !v1.equals(v2);
            } else {
                return v2 != null;
            }
        }

    }

    static class ToBooleanListFunc<S> extends AbstractFuncWithHints<Collection<S>, List<Boolean>> {

        private final Class<S> sourceType;
        private final S valueTrue;
        private final S valueFalse;

        public ToBooleanListFunc(Class<S> sourceType, S valueTrue, S valueFalse) {
            this.sourceType = sourceType;
            this.valueTrue = valueTrue;
            this.valueFalse = valueFalse;
        }

        @Override
        public List<Boolean> apply(Collection<S> source) {
            if (source == null) {
                return null;
            }

            List<Boolean> values = new ArrayList<>(source.size());
            for (S e : source) {
                values.add(valueTrue.equals(e));
            }
            return values;
        }

    }

    static class ToBooleanSetFunc<S> extends AbstractFuncWithHints<Collection<S>, Set<Boolean>> {

        private final Class<S> sourceType;
        private final S valueTrue;
        private final S valueFalse;

        public ToBooleanSetFunc(Class<S> sourceType, S valueTrue, S valueFalse) {
            this.sourceType = sourceType;
            this.valueTrue = valueTrue;
            this.valueFalse = valueFalse;
        }

        @Override
        public Set<Boolean> apply(Collection<S> source) {
            if (source == null) {
                return null;
            }

            Set<Boolean> values = new LinkedHashSet<>(source.size());
            for (S e : source) {
                values.add(valueTrue.equals(e));
            }
            return values;
        }

    }

    private Func createAndFunc(TreeNode spec) {
        List<Func<Object, Boolean>> valueFuncList = new ArrayList<>(spec.getChildren().size());
        for (TreeNode child : spec.getChildren()) {
            valueFuncList.add(masterFuncFactory.create(child));
        }
        return new AndFunc(valueFuncList);
    }

    private Func createEqualFunc(TreeNode spec) {
        final int operandCount = spec.getChildrenCount();

        Func<Object, Object> operand1;
        Func<Object, Object> operand2;
        if (operandCount == 0) {
            throw new CreationException(spec.toExpr() + " needs at least one operand value func");
        } else if (operandCount == 1) {
            operand1 = masterFuncFactory.create("_");
            operand2 = masterFuncFactory.create(spec.getChildAt(0));
        } else {
            operand1 = masterFuncFactory.create(spec.getChildAt(0));
            operand2 = masterFuncFactory.create(spec.getChildAt(1));
        }
        return new EqualFunc(operand1, operand2);
    }

    private Func createNotEqualFunc(TreeNode spec) {
        Func<Object, Object> operand1 = masterFuncFactory.create(spec.getChildAt(0));
        Func<Object, Object> operand2 = masterFuncFactory.create(spec.getChildAt(1));
        return new NotEqualFunc(operand1, operand2);
    }

    private Func createNotFunc(TreeNode spec) {
        Func<Object, Boolean> operand = masterFuncFactory.create(spec.getChildAt(0));
        return new NotFunc(operand);
    }

    private Func createOrFunc(TreeNode spec) {
        List<Func<Object, Boolean>> operands = new ArrayList<>(spec.getChildren().size());
        for (TreeNode child : spec.getChildren()) {
            operands.add(masterFuncFactory.create(child));
        }
        return new OrFunc(operands);
    }

    private Func createFromFunc(TreeNode spec) {
        String sourceType = spec.getDataOfChildAt(0);
        String valueTrue = spec.getDataOfChildAt(1);
        String valueFalse = spec.getDataOfChildAt(2);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new FromFunc<>(String.class, valueTrue, valueFalse);
        }

        if (ProtoTypes.DOUBLE.matchIgnoreCase(sourceType)) {
            Double vTrue = Double.parseDouble(valueTrue);
            Double vFalse = Double.parseDouble(valueFalse);
            return new FromFunc<>(Double.class, vTrue, vFalse);
        }

        if (ProtoTypes.FLOAT.matchIgnoreCase(sourceType)) {
            Float vTrue = Float.parseFloat(valueTrue);
            Float vFalse = Float.parseFloat(valueFalse);
            return new FromFunc<>(Float.class, vTrue, vFalse);
        }

        if (ProtoTypes.INTEGER.matchIgnoreCase(sourceType)) {
            Integer vTrue = Integer.parseInt(valueTrue);
            Integer vFalse = Integer.parseInt(valueFalse);
            return new FromFunc<>(Integer.class, vTrue, vFalse);
        }

        if (ProtoTypes.LONG.matchIgnoreCase(sourceType)) {
            Long vTrue = Long.parseLong(valueTrue);
            Long vFalse = Long.parseLong(valueFalse);
            return new FromFunc<>(Long.class, vTrue, vFalse);
        }

        if (ProtoTypes.SHORT.matchIgnoreCase(sourceType)) {
            Short vTrue = Short.parseShort(valueTrue);
            Short vFalse = Short.parseShort(valueFalse);
            return new FromFunc<>(Short.class, vTrue, vFalse);
        }

        throw new CreationException(sourceType + " specified in value function spec as source type is not supported");
    }

    private Func createLiteralFunc(TreeNode spec) {
        // BOOL(literal)
        final String literal = spec.getDataOfChildAt(0);
        return masterFuncFactory.create(String.format("VAL(Boolean,%s)", literal));
    }

    private Func createListFromFunc(TreeNode spec) {
        String sourceType = spec.getDataOfChildAt(0);
        String valueTrue = spec.getDataOfChildAt(1);
        String valueFalse = spec.getDataOfChildAt(2);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new ToBooleanListFunc<>(String.class, valueTrue, valueFalse);
        }

        if (ProtoTypes.DOUBLE.matchIgnoreCase(sourceType)) {
            Double vTrue = Double.parseDouble(valueTrue);
            Double vFalse = Double.parseDouble(valueFalse);
            return new ToBooleanListFunc<>(Double.class, vTrue, vFalse);
        }

        if (ProtoTypes.FLOAT.matchIgnoreCase(sourceType)) {
            Float vTrue = Float.parseFloat(valueTrue);
            Float vFalse = Float.parseFloat(valueFalse);
            return new ToBooleanListFunc<>(Float.class, vTrue, vFalse);
        }

        if (ProtoTypes.INTEGER.matchIgnoreCase(sourceType)) {
            Integer vTrue = Integer.parseInt(valueTrue);
            Integer vFalse = Integer.parseInt(valueFalse);
            return new ToBooleanListFunc<>(Integer.class, vTrue, vFalse);
        }

        if (ProtoTypes.LONG.matchIgnoreCase(sourceType)) {
            Long vTrue = Long.parseLong(valueTrue);
            Long vFalse = Long.parseLong(valueFalse);
            return new ToBooleanListFunc<>(Long.class, vTrue, vFalse);
        }

        if (ProtoTypes.SHORT.matchIgnoreCase(sourceType)) {
            Short vTrue = Short.parseShort(valueTrue);
            Short vFalse = Short.parseShort(valueFalse);
            return new ToBooleanListFunc<>(Short.class, vTrue, vFalse);
        }

        throw new CreationException(sourceType + " as source type specified in value function spec is not supported");
    }

    private Func createSetFromFunc(TreeNode spec) {
        String sourceType = spec.getDataOfChildAt(0);
        String valueTrue = spec.getDataOfChildAt(1);
        String valueFalse = spec.getDataOfChildAt(2);

        if (ProtoTypes.STRING.matchIgnoreCase(sourceType)) {
            return new ToBooleanSetFunc<>(String.class, valueTrue, valueFalse);
        }

        if (ProtoTypes.DOUBLE.matchIgnoreCase(sourceType)) {
            Double vTrue = Double.parseDouble(valueTrue);
            Double vFalse = Double.parseDouble(valueFalse);
            return new ToBooleanSetFunc<>(Double.class, vTrue, vFalse);
        }

        if (ProtoTypes.FLOAT.matchIgnoreCase(sourceType)) {
            Float vTrue = Float.parseFloat(valueTrue);
            Float vFalse = Float.parseFloat(valueFalse);
            return new ToBooleanSetFunc<>(Float.class, vTrue, vFalse);
        }

        if (ProtoTypes.INTEGER.matchIgnoreCase(sourceType)) {
            Integer vTrue = Integer.parseInt(valueTrue);
            Integer vFalse = Integer.parseInt(valueFalse);
            return new ToBooleanSetFunc<>(Integer.class, vTrue, vFalse);
        }

        if (ProtoTypes.LONG.matchIgnoreCase(sourceType)) {
            Long vTrue = Long.parseLong(valueTrue);
            Long vFalse = Long.parseLong(valueFalse);
            return new ToBooleanSetFunc<>(Long.class, vTrue, vFalse);
        }

        if (ProtoTypes.SHORT.matchIgnoreCase(sourceType)) {
            Short vTrue = Short.parseShort(valueTrue);
            Short vFalse = Short.parseShort(valueFalse);
            return new ToBooleanSetFunc<>(Short.class, vTrue, vFalse);
        }

        throw new CreationException(sourceType + " as source type specified in value function spec is not supported");
    }

}
