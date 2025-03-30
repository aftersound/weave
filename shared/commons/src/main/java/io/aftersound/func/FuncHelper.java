package io.aftersound.func;


import io.aftersound.util.ExprTreeParsingException;
import io.aftersound.util.Handle;
import io.aftersound.util.TreeNode;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

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

    /**
     * Retrieves a required dependency identified by the given ID and type. If the dependency is not found,
     * an {@link IllegalStateException} is thrown. The method relies on the {@link Handle} mechanism for managing
     * singleton or multi-ton instances.
     *
     * @param <T>  the type of the required dependency
     * @param id   the identifier uniquely specifying the dependency
     * @param type the class type of the required dependency
     * @return the required dependency instance of type T
     * @throws IllegalStateException if the required dependency is not available
     */
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

    /**
     * Creates a {@link Func} based on the provided target type and function factory.
     * Depending on the target type, the method generates an appropriate function specification.
     * If the target type is recognized, the corresponding function is created using the given {@link FuncFactory}.
     * If the target type is unrecognized, the method returns null.
     *
     * @param targetType the type of target to be parsed, such as "BOOLEAN", "STRING", "INTEGER", etc.
     * @param funcFactory the factory used to create the {@link Func} based on the generated specification
     * @return a {@link Func} that can parse input based on the specified target type, or null if the target type is unrecognized
     */
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

    /**
     * Decodes the given string if it is encoded using specific encoding patterns.
     * <p>
     * If the input string starts with "BASE64|", it is assumed to be Base64-encoded,
     * and the method decodes the Base64 content and returns the decoded result as a UTF-8 string.
     * <p>
     * If the input string starts with "URL|", it is assumed to be URL-encoded,
     * and the method decodes the URL content and returns the decoded result as a UTF-8 string.
     * <p>
     * If the input string does not match the expected encoding patterns or is null,
     * the method returns the input string as is.
     *
     * @param str the input string to be decoded, which may be encoded with "BASE64|" or "URL|" prefix
     * @return the decoded string if the input is encoded, or the original string if no encoding is detected or the input is null
     */
    public static String decodeIfEncoded(String str) {
        if (str != null) {
            if (str.startsWith("BASE64|")) {
                return new String(Base64.getDecoder().decode(str.substring(7)), StandardCharsets.UTF_8);
            } else if (str.startsWith("URL|")) {
                return URLDecoder.decode(str.substring(7), StandardCharsets.UTF_8);
            }
        }
        return str;
    }

}
