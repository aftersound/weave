package io.aftersound.expr;

import io.aftersound.util.TreeNode;

public class NOTTranslator extends Translator {

    @Override
    public String getOperator() {
        return "NOT";
    }

    @Override
    public Expr apply(TreeNode expr) {
        if (expr.getChildrenCount() != 1) {
            String msg = String.format(
                    "'%s' is malformed expr. 'NOT' expression takes 1 and only 1 operand",
                    expr.toExpr()
            );
            throw new IllegalArgumentException(msg);
        }

        Expr filter = leader.apply(expr.getChildAt(0));
        return Expr.not(filter);
    }

}
