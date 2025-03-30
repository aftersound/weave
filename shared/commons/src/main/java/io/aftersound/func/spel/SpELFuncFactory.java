package io.aftersound.func.spel;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.Func;
import io.aftersound.func.FuncHelper;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.util.TreeNode;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SpELFuncFactory extends MasterAwareFuncFactory {

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("SpEL:EVAL".equals(funcName)) {
            return createEvalFunc(spec);
        }

        return null;
    }

    private static class EvalFunc extends AbstractFuncWithHints<Object, Object> {

        private final Expression expression;

        public EvalFunc(Expression expression) {
            this.expression = expression;
        }

        @Override
        public Object apply(Object source) {
            return expression.getValue(source);
        }

    }

    private Func createEvalFunc(TreeNode spec) {
        final String exprText = spec.getDataOfChildAt(0);
        try {
            if (exprText == null || exprText.isEmpty()) {
                throw new IllegalArgumentException("'expression' cannot be null or empty");
            }
            String decoded = FuncHelper.decodeIfEncoded(exprText);
            Expression expression = PARSER.parseExpression(decoded);
            return new EvalFunc(expression);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "SpEL:EVAL(expression)",
                    "SpEL:EVAL(user.firstName)",
                    e
            );
        }
    }

}
