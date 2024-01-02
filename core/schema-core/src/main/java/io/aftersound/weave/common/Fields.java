package io.aftersound.weave.common;

import io.aftersound.weave.utils.*;

import java.util.*;

/**
 * Helper which wraps around a list of {@link Field}s and provides easy access/lookup
 */
public final class Fields {

    protected final Map<String, Triplet<Field, TreeNode, ValueFunc>> fieldByFieldName;

    private Fields(Map<String, Triplet<Field, TreeNode, ValueFunc>> fieldByFieldName) {
        this.fieldByFieldName = Collections.unmodifiableMap(fieldByFieldName);
    }

    public static Fields from(List<Field> fieldList) {
        Map<String, Triplet<Field, TreeNode, ValueFunc>> tripletByFieldName = new LinkedHashMap<>(fieldList.size());
        for (Field field : fieldList) {
            // parse field.fun into TreeNode and create ValueFunc based on func spec
            TreeNode treeNode = null;
            ValueFunc valueFunc = null;
            if (field.getFunc() != null) {
                try {
                    treeNode = TextualExprTreeParser.parse(field.getFunc());
                } catch (ExprTreeParsingException e) {
                    String msg = String.format("'%s': malformed func spec for field '%s'", field.getFunc(), field.getName());
                    throw new IllegalArgumentException(msg, e);
                }

                // create ValueFunc based on value spec TreeNode
                valueFunc = MasterValueFuncFactory.create(treeNode);
            }

            tripletByFieldName.put(field.getName(), Triplet.of(field, treeNode, valueFunc));
        }
        return new Fields(tripletByFieldName);
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

}
