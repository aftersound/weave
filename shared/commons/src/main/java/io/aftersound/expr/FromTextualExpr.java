package io.aftersound.expr;

import io.aftersound.dict.Dictionary;
import io.aftersound.util.TreeNode;

import java.util.function.Function;

public class FromTextualExpr implements Function<String, Expr> {

    private final Dictionary<?> dictionary;

    public FromTextualExpr(Dictionary<?> dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public Expr apply(String expr) {
        final TreeNode treeNode;
        try {
            treeNode = TreeNode.from(expr);
        } catch (Exception e) {
            String msg = String.format(
                    "'%s' is malformed",
                    expr
            );
            throw new IllegalArgumentException(msg, e);
        }
        return new FromTreeNode(dictionary).apply(treeNode);
    }

}