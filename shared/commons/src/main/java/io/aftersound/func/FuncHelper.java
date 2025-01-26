package io.aftersound.func;


import io.aftersound.util.ExprTreeParsingException;
import io.aftersound.util.Handle;
import io.aftersound.util.TreeNode;

public final class FuncHelper {

    public static TreeNode parseAndValidate(String funcDirective, String funcName) {
        if (funcDirective == null || funcDirective.isEmpty()) {
            throw new CreationException("Given function directive is null or empty");
        }

        TreeNode treeNode;
        try {
            treeNode = TreeNode.from(funcDirective);
        } catch (ExprTreeParsingException e) {
            throw new CreationException("Given function directive " + funcDirective + " is malformed", e);
        }

        if (!treeNode.getData().equals(funcName)) {
            throw new CreationException("Given value function spec " + funcDirective + " is not expected");
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
