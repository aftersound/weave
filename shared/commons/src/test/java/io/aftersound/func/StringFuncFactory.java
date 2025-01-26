package io.aftersound.func;

import io.aftersound.schema.Field;
import io.aftersound.util.TreeNode;

import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class StringFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = List.of(
            Descriptor.builder("STR")
                    .withAliases("STRING")
                    .withControls(
                            Field.stringFieldBuilder("literal").build()
                    )
                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {
        switch (directive.getData()) {
            case "STR": {
                return createLiteralFunc(directive);
            }
            case "STR:IS_NULL_OR_EMPTY": {
                return createIsNullOrEmptyFunc(directive);
            }
            case "STR:LENGTH": {
                return createLengthFunc(directive);
            }
            case "STR:LENGTH_WITHIN": {
                return createLengthWithinFunc(directive);
            }
            case "STR:LOWER_CASE": {
                return createLowerCaseFunc(directive);
            }
            case "STR:UPPER_CASE": {
                return createUpperCaseFunc(directive);
            }
        }

        return null;
    }

    private Func createLiteralFunc(TreeNode directive) {
        final String literal = directive.getDataOfChildAt(0);
        return new LiteralFunc(literal);
    }

    private Func createIsNullOrEmptyFunc(TreeNode directive) {
        return IsNullOrEmptyFunc.INSTANCE;
    }

    private Func createLengthFunc(TreeNode directive) {
        return LengthFunc.INSTANCE;
    }

    private Func createLengthWithinFunc(TreeNode directive) {
        final String l1 = directive.getDataOfChildAt(0);
        final String l2 = directive.getDataOfChildAt(1);
        try {
            int l = Integer.parseInt(l1);
            int u = Integer.parseInt(l2);
            return new LengthWithinFunc(l, u);
        } catch (Exception e) {
            throw new CreationException(directive + " is not valid");
        }
    }

    private Func createLowerCaseFunc(TreeNode directive) {
        return ToLowerCaseFunc.INSTANCE;
    }

    private Func createUpperCaseFunc(TreeNode directive) {
        return ToUpperCaseFunc.INSTANCE;
    }

    private static class LiteralFunc implements Func<Object, String> {

        private final String literal;

        public LiteralFunc(String literal) {
            this.literal = literal;
        }

        @Override
        public String apply(Object s) {
            return literal;
        }

    }

    private static class IsNullOrEmptyFunc implements Func<String, Boolean> {

        public static final Func<String, Boolean> INSTANCE = new IsNullOrEmptyFunc();

        @Override
        public Boolean apply(String s) {
            return s == null || s.isEmpty();
        }

    }

    private static class LengthFunc implements Func<String, Integer> {

        public static final Func<String, Integer> INSTANCE = new LengthFunc();

        @Override
        public Integer apply(String s) {
            return s != null ? s.length() : 0;
        }

    }

    private static class LengthWithinFunc implements Func<String, Boolean> {

        private final int l;
        private final int u;

        public LengthWithinFunc(int l, int u) {
            this.l = l;
            this.u = u;
        }

        @Override
        public Boolean apply(String s) {
            return s != null && s.length() >= l && s.length() <= u;
        }

    }

    private static class ToLowerCaseFunc implements Func<String, String> {

        public static final Func<String, String> INSTANCE = new ToLowerCaseFunc();

        @Override
        public String apply(String s) {
            return s != null ? s.toLowerCase() : null;
        }

    }

    private static class ToUpperCaseFunc implements Func<String, String> {

        public static final Func<String, String> INSTANCE = new ToUpperCaseFunc();

        @Override
        public String apply(String s) {
            return s != null ? s.toUpperCase() : null;
        }

    }

}
