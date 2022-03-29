package io.aftersound.weave.common;

import io.aftersound.weave.utils.*;

import java.util.*;

/**
 * Helper which wraps around a list of {@link Field}s and provides easy access/lookup
 */
public final class Fields {

    private static final boolean WITH_VALUE_TYPE = true;
    private static final boolean WITHOUT_VALUE_TYPE = false;

    protected final Map<String, Triplet<Field, TreeNode, ValueFunc>> fieldByFieldName;

    private Fields(Map<String, Triplet<Field, TreeNode, ValueFunc>> fieldByFieldName) {
        this.fieldByFieldName = Collections.unmodifiableMap(fieldByFieldName);
    }

    public static Fields from(List<Field> fieldList, ValueFuncRegistry valueFuncRegistry) {
        Map<String, Triplet<Field, TreeNode, ValueFunc>> tripletByFieldName = new LinkedHashMap<>(fieldList.size());
        for (Field field : fieldList) {
            // parse field.valueFuncSpec into TreeNode
            TreeNode treeNode;
            try {
                treeNode = TextualExprTreeParser.parse(field.getValueFunc());
            } catch (ExprTreeParsingException e) {
                throw new IllegalArgumentException("Malformed field value spec: " + field.getValueFunc(), e);
            }

            // create ValueFunc based on value spec TreeNode
            ValueFunc valueFunc = valueFuncRegistry.getValueFunc(field.getValueFunc());

            tripletByFieldName.put(field.getName(), Triplet.of(field, treeNode, valueFunc));
        }
        return new Fields(tripletByFieldName);
    }

    public static Fields fromSpecWithValueType(List<TreeNode> fieldSpecs, ValueFuncRegistry valueFuncRegistry) {
        List<Field> fieldList = new ArrayList<>(fieldSpecs.size());
        for (TreeNode fieldSpec : fieldSpecs) {
            fieldList.add(createField(fieldSpec, WITH_VALUE_TYPE));
        }
        return Fields.from(fieldList, valueFuncRegistry);
    }

    public static Fields fromSpecWithoutValueType(List<TreeNode> fieldSpecs, ValueFuncRegistry valueFuncRegistry) {
        List<Field> fieldList = new ArrayList<>(fieldSpecs.size());
        for (TreeNode fieldSpec : fieldSpecs) {
            fieldList.add(createField(fieldSpec, WITHOUT_VALUE_TYPE));
        }
        return Fields.from(fieldList, valueFuncRegistry);
    }

    public static Fields fromSpecWithValueType(String fieldsSpecExpr, ValueFuncRegistry valueFuncRegistry) {
        TreeNode fieldsSpec;
        try {
            fieldsSpec = TextualExprTreeParser.parse(fieldsSpecExpr);
        } catch (ExprTreeParsingException e) {
            throw new IllegalArgumentException("Exception occurred on parsing " + fieldsSpecExpr, e);
        }
        return fromSpecWithValueType(fieldsSpec.getChildren(), valueFuncRegistry);
    }

    public static Fields fromSpecWithoutValueType(String fieldsSpecExpr, ValueFuncRegistry valueFuncRegistry) {
        TreeNode fieldsSpec;
        try {
            fieldsSpec = TextualExprTreeParser.parse(fieldsSpecExpr);
        } catch (ExprTreeParsingException e) {
            throw new IllegalArgumentException("Exception occurred on parsing " + fieldsSpecExpr, e);
        }
        return fromSpecWithoutValueType(fieldsSpec.getChildren(), valueFuncRegistry);
    }

    public <T> T invite(Visitor<Triplet<Field, TreeNode, ValueFunc>, T> visitor) {
        for (Map.Entry<String, Triplet<Field,TreeNode,ValueFunc>> e : fieldByFieldName.entrySet()) {
            visitor.visit(e.getValue());
        }
        return visitor.getVisited();
    }

    public int size() {
        return fieldByFieldName.size();
    }

    public Collection<String> getFieldNames() {
        return fieldByFieldName.keySet();
    }

    public Field getField(String fieldName) {
        Triplet<Field, TreeNode, ValueFunc> fieldTriple = fieldByFieldName.get(fieldName);
        return fieldTriple != null ? fieldTriple.first() : null;
    }

    public ValueFunc<Object, Object> getValueFunc(String fieldName) {
        Triplet<Field, TreeNode, ValueFunc> fieldTriple = fieldByFieldName.get(fieldName);
        return fieldTriple != null ? fieldTriple.third() : null;
    }

    private static Field createField(TreeNode fieldSpec, boolean withValueType) {
        Field f = new Field();

        String name, valueType, valueFuncSpec, sourceField;

        List<TreeNode> children = fieldSpec.getChildren();
        if (withValueType) {
            // FIELD(<name>,<valueType>,<valueSpec>,[sourceField])
            name = children.get(0).getData();
            valueType = children.get(1).getData();
            valueFuncSpec = children.get(2).toString();
            sourceField = children.size() >= 4 ? children.get(3).getData() : null;
        } else {
            // FIELD(<name>,<valueSpec>,[sourceField])
            name = children.get(0).getData();
            valueType = null;
            valueFuncSpec = children.get(1).toString();
            sourceField = children.size() >= 3 ? children.get(2).getData() : null;
        }

        f.setName(name);
        f.setType(valueType);
        f.setValueFunc(valueFuncSpec);
        f.setSource(sourceField);

        return f;
    }

}
