package io.aftersound.expr;

import io.aftersound.dict.Dictionary;
import io.aftersound.util.TreeNode;

import java.util.*;
import java.util.function.Function;

public class TranslatorFactory {

    public static Function<TreeNode, Expr> getTranslator(Dictionary<?> dictionary) {
        return new LeaderTranslator(
                dictionary,
                new ANDTranslator(),
                new EQTranslator(),
                new GETranslator(),
                new GTTranslator(),
                new INTranslator(),
                new LETranslator(),
                new LTTranslator(),
                new NETranslator(),
                new NOTINTranslator(),
                new NOTTranslator(),
                new ORTranslator()
        );
    }

    private static class LeaderTranslator implements Function<TreeNode, Expr> {

        private final Map<String, Translator> subordinateByOperator;

        public LeaderTranslator(Dictionary<?> dictionary, Translator... subordinates) {
            Map<String, Translator> subordinateByOperator = new HashMap<>();
            for (Translator subordinate : subordinates) {
                subordinateByOperator.put(subordinate.getOperator(), subordinate.bind(dictionary).bind(this));
            }

            this.subordinateByOperator = Collections.unmodifiableMap(subordinateByOperator);
        }

        @Override
        public Expr apply(TreeNode treeNode) {
            final String op = treeNode.getData();
            Function<TreeNode, Expr> translator = subordinateByOperator.get(op);
            if (translator != null) {
                return translator.apply(treeNode);
            } else {
                String msg = String.format(
                        "'%s' has operator as '%s', which is not supported",
                        treeNode.toExpr(),
                        op
                );
                throw new IllegalArgumentException(msg);
            }
        }

    }

}
