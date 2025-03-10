package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.util.TreeNode;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
public class SpELFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = DescriptorHelper.getDescriptors(SpELFuncFactory.class);

    private static final SpelExpressionParser PARSER = new SpelExpressionParser();

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("SpEL:XFC".equals(funcName)) {
            return createExtractFromContextFunc(spec);
        }

        if ("SpEL:XFO".equals(funcName)) {
            return createExtractFromObjectFunc(spec);
        }

        return null;
    }

    private static class ExtractFromContextFunc extends AbstractFuncWithHints<EvaluationContext, Object> {

        private final Expression expression;

        public ExtractFromContextFunc(Expression expression) {
            this.expression = expression;
        }

        @Override
        public Object apply(EvaluationContext source) {
            if (source != null) {
                return expression.getValue(source);
            } else {
                return null;
            }
        }

    }

    private static class ExtractFromObjectFunc extends AbstractFuncWithHints<Object, Object> {

        private final Expression expression;

        public ExtractFromObjectFunc(Expression expression) {
            this.expression = expression;
        }

        @Override
        public Object apply(Object source) {
            if (source != null) {
                return expression.getValue(source);
            } else {
                return null;
            }
        }

    }

    private Func createExtractFromContextFunc(TreeNode spec) {
        try {
            Expression expression = parseExtractionExpression(spec);
            return new ExtractFromContextFunc(expression);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "SpEL:XFC(expression)",
                    "SpEL:XFC(user.firstName)",
                    e
            );
        }
    }

    private Func createExtractFromObjectFunc(TreeNode spec) {
        try {
            Expression expression = parseExtractionExpression(spec);
            return new ExtractFromObjectFunc(expression);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "SpEL:XFO(expression)",
                    "SpEL:XFO(user.firstName)",
                    e
            );
        }
    }

    private Expression parseExtractionExpression(TreeNode spec) {
        final String textualExpr = spec.getDataOfChildAt(0);
        try {
            return PARSER.parseExpression(textualExpr);
        } catch (Exception e) {
            String msg = String.format("'%s' specified in '%s' cannot be parsed", textualExpr, spec);
            throw new IllegalArgumentException(msg, e);
        }
    }

}
