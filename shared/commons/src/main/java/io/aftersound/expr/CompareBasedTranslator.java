package io.aftersound.expr;

import io.aftersound.util.TreeNode;

public abstract class CompareBasedTranslator extends Translator {

    @Override
    public final Expr apply(TreeNode expr) {
        final String op = getOperator();

        if (expr.getChildrenCount() != 2) {
            String msg = String.format(
                    "'%s' is malformed expr. '%s' expression takes 2 and only 2 operands",
                    expr.toExpr(),
                    op
            );
            throw new IllegalArgumentException(msg);
        }
        String fieldName = expr.getDataOfChildAt(0);
        String literal = expr.getDataOfChildAt(1);
        String fieldType = dictionary.getAttribute(fieldName, "TYPE");

        if (fieldType == null) {
            String msg = String.format(
                    "'%s' cannot be translated because field '%s' is not in defined dictionary",
                    expr.toExpr(),
                    fieldName
            );
            throw new IllegalArgumentException(msg);
        }

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

        return Expr.compare(op, fieldName, value);

    }

}
