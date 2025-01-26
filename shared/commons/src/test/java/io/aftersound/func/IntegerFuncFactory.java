package io.aftersound.func;

import io.aftersound.util.TreeNode;

@SuppressWarnings({"unchecked", "rawtypes"})
public class IntegerFuncFactory extends MasterAwareFuncFactory {

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {

        if ("INT:LE".equals(directive.getData())) {
            return createLEFunc(directive);
        }

        if ("INT:LT".equals(directive.getData())) {
            return createLTFunc(directive);
        }

        if ("INT:GE".equals(directive.getData())) {
            return createGEFunc(directive);
        }

        if ("INT:GT".equals(directive.getData())) {
            return createGTFunc(directive);
        }

        return null;
    }

    private Func createLEFunc(TreeNode directive) {
        final String literal = directive.getDataOfChildAt(0);
        return new LEFunc(Integer.parseInt(literal));
    }

    private Func createLTFunc(TreeNode directive) {
        final String literal = directive.getDataOfChildAt(0);
        return new LTFunc(Integer.parseInt(literal));
    }

    private Func createGEFunc(TreeNode directive) {
        final String literal = directive.getDataOfChildAt(0);
        return new GEFunc(Integer.parseInt(literal));
    }

    private Func createGTFunc(TreeNode directive) {
        final String literal = directive.getDataOfChildAt(0);
        return new GTFunc(Integer.parseInt(literal));
    }

    private static class LTFunc implements Func<Integer, Boolean> {

        private final int target;

        public LTFunc(int target) {
            this.target = target;
        }

        @Override
        public Boolean apply(Integer s) {
            return s != null && s < target;
        }

    }

    private static class LEFunc implements Func<Integer, Boolean> {

        private final int target;

        public LEFunc(int target) {
            this.target = target;
        }

        @Override
        public Boolean apply(Integer s) {
            return s != null && s <= target;
        }

    }

    private static class GTFunc implements Func<Integer, Boolean> {

        private final int target;

        public GTFunc(int target) {
            this.target = target;
        }

        @Override
        public Boolean apply(Integer s) {
            return s != null && s > target;
        }

    }

    private static class GEFunc implements Func<Integer, Boolean> {

        private final int target;

        public GEFunc(int target) {
            this.target = target;
        }

        @Override
        public Boolean apply(Integer s) {
            return s != null && s >= target;
        }

    }

}
