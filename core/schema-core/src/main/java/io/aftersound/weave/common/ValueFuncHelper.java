package io.aftersound.weave.common;

import io.aftersound.weave.utils.ExprTreeParsingException;
import io.aftersound.weave.utils.Handle;
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

    public static <T> T getRequiredDependency(String id, Class<T> type) {
        T required = Handle.of(id, type).get();
        if (required == null) {
            throw new IllegalStateException(
                    String.format(
                            "Implicit but required runtime dependency with id '%s' and of type '%s' is not available",
                            id,
                            type.getName()
                    )
            );
        }
        return required;
    }

    public static void assertNotNull(Object value, String format, Object... args) {
        if (value == null) {
            throw new IllegalArgumentException(String.format(format, args));
        }
    }

}
