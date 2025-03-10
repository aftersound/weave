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
                            "Implicit but required runtime dependency { id: '%s', type: '%s' } is not available",
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

    public static CreationException createCreationException(TreeNode spec, String desired, String example) {
        return new CreationException(
                String.format(
                        "'%s' is invalid, expecting '%s' like '%s'",
                        spec,
                        desired,
                        example
                )
        );
    }

    public static CreationException createCreationException(TreeNode spec, String desired, String example, Throwable cause) {
        return new CreationException(
                String.format(
                        "'%s' is invalid, expecting '%s' like '%s'",
                        spec,
                        desired,
                        example
                ),
                cause
        );
    }

    public static Func<String, Object> createParseFunc(String targetType, FuncFactory funcFactory) {
        String funcSpec;
        switch (targetType.toUpperCase()) {
            case "BOOLEAN": {
                funcSpec = "BOOL:FROM(String,true,false)";
                break;
            }
            case "DOUBLE": {
                funcSpec = "DOUBLE:FROM(String)";
                break;
            }
            case "FLOAT": {
                funcSpec = "FLOAT:FROM(String)";
                break;
            }
            case "INTEGER": {
                funcSpec = "INTEGER:FROM(String)";
                break;
            }
            case "LONG": {
                funcSpec = "LONG:FROM(String)";
                break;
            }
            case "SHORT": {
                funcSpec = "SHORT:FROM(String)";
                break;
            }
            case "STRING": {
                funcSpec = "_()";
                break;
            }
            default: {
                funcSpec = null;
            }
        }
        return funcSpec != null ? funcFactory.create(funcSpec) : null;
    }

}
