package io.aftersound.weave.common;

import io.aftersound.weave.utils.*;

import java.util.ArrayList;
import java.util.List;

public class FieldsRegistry extends Registry<String, Fields> {

    private static final String FIELDS = "Fields";

    /**
     * Fields(Field(name,type,valueSpec,[source]),...)
     *
     * @param fieldsSpec specification of fields
     * @return {@link Fields} which conforms to given specification
     */
    public Fields getFields(String fieldsSpec) {
        return super.get(
                fieldsSpec,
                new Factory<String, Fields>() {
                    @Override
                    public Fields create(String spec) {
                        try {
                            TreeNode specTreeNode = TextualExprTreeParser.parse(spec);
                            if (FIELDS.equals(specTreeNode.getData()) && specTreeNode.getChildren() != null) {
                                return createFields(specTreeNode.getChildren());
                            } else {
                                throw new IllegalArgumentException(spec + " is not a valid specification for Fields");
                            }
                        } catch (ExprTreeParsingException e) {
                            throw new IllegalArgumentException(spec + " is not a valid specification for Fields");
                        }
                    }
                }
        );
    }

    private Fields createFields(List<TreeNode> fieldSpecList) {
        List<Field> fieldList = new ArrayList<>(fieldSpecList.size());
        for (TreeNode fieldSpec : fieldSpecList) {
            Field field = createField(fieldSpec);
            fieldList.add(field);
        }
        return Fields.of(fieldList);
    }

    private Field createField(TreeNode fieldSpec) {
        List<TreeNode> children = fieldSpec.getChildren();
        String name = children.size() > 0 ? children.get(0).getData() : null;
        String type = children.size() > 1 ? children.get(1).getData() : null;
        String valueSpec = children.size() > 2 ? children.get(2).toExpr() : null;
        String source = children.size() > 3 ? children.get(3).toExpr() : null;
        return Field.of(name, type, valueSpec, source);
    }

}
