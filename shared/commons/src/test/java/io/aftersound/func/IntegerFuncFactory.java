package io.aftersound.func;

import io.aftersound.util.TreeNode;

@SuppressWarnings({"unchecked", "rawtypes"})
public class IntegerFuncFactory implements FuncFactory {

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {
        if ("INT:LT".equals(directive.getData())) {
            return createLessThanFunc(directive);
        }

        return null;
    }

    private Func createLessThanFunc(TreeNode directive) {
        final String literal = directive.getDataOfChildAt(0);
        return new LessThanFunc(Integer.parseInt(literal));
    }

    private static class LessThanFunc implements Func<Integer, Boolean> {

        private final int target;

        public LessThanFunc(int target) {
            this.target = target;
        }

        @Override
        public Boolean apply(Integer s) {
            return s != null && s < target;
        }

    }

}
