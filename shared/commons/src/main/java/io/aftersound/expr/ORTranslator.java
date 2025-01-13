package io.aftersound.expr;

import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.List;

public class ORTranslator extends Translator {

    @Override
    protected String getOperator() {
        return "OR";
    }

    @Override
    public Expr apply(TreeNode expr) {
        if (expr.getChildrenCount() == 0) {
            String msg = String.format(
                    "'%s' is malformed expr. 'OR' expression takes at least 1 operand",
                    expr.toExpr()
            );
            throw new IllegalArgumentException(msg);
        }

        List<Expr> operands = new ArrayList<>(expr.getChildrenCount());
        for (TreeNode child : expr.getChildren()) {
            operands.add(leader.apply(child));
        }
        return Expr.or(operands);
    }

}
