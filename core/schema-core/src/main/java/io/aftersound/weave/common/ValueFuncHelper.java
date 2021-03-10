package io.aftersound.weave.common;

import io.aftersound.weave.utils.ExprTreeParsingException;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

public final class ValueFuncHelper {

    public static TreeNode parseAndValidate(String valueFuncSpec, String valueFuncName) {
        if (valueFuncSpec == null || valueFuncSpec.isEmpty()) {
            throw new ValueFuncException("Given value function spec is null or empty");
        }

        TreeNode treeNode;
        try {
            treeNode = TextualExprTreeParser.parse(valueFuncSpec);
        } catch (ExprTreeParsingException e) {
            throw new ValueFuncException("Given value function spec " + valueFuncSpec + " is malformed", e);
        }

        if (!treeNode.getData().equals(valueFuncName)) {
            throw new ValueFuncException("Given value function spec " + valueFuncSpec + " is not expected");
        }

        return treeNode;
    }

}
