package io.aftersound.expr;

import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class INTranslator extends Translator {

    @Override
    public String getOperator() {
        return "IN";
    }

    @Override
    public Expr apply(TreeNode expr) {
        String fieldName = expr.getDataOfChildAt(0);
        List<String> literals = expr.getDataOfChildren(1);

        String fieldType = dictionary.getAttribute(fieldName, "type");
        if (fieldType == null) {
            String msg = String.format(
                    "'%s' cannot be translated because field '%s' is not in defined dictionary",
                    expr.toExpr(),
                    fieldName
            );
            throw new IllegalArgumentException(msg);
        }

        List<Object> values = new ArrayList<>(literals.size());
        for (String literal : literals) {
            Object value;
            try {
                value = parseValue(literal, fieldType);
            } catch (Exception e) {
                String msg = String.format(
                        "'%s' cannot be translated because literal '%s' cannot be parsed to type '%s'",
                        expr.toExpr(),
                        literal,
                        fieldType
                );
                throw new IllegalArgumentException(msg, e);
            }
            values.add(value);
        }

        return Expr.in(fieldName, values);
    }

}
