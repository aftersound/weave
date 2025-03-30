package io.aftersound.func.mvel;

import io.aftersound.func.*;
import io.aftersound.util.ResourceRegistry;
import io.aftersound.util.TreeNode;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static io.aftersound.func.FuncHelper.getRequiredDependency;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MVEL2FuncFactory extends MasterAwareFuncFactory {

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        switch (funcName) {
            case "MVEL2:EVAL": {
                return createEvalFunc(spec);
            }
            case "MVEL2:EVAL1": {
                return createEval1Func(spec);
            }
            default: {
                return null;
            }
        }
    }

    static class EvalFunc extends AbstractFuncWithHints<Object, Object> implements ResourceAware {

        private final String inputName;
        private Serializable compiledExpression;

        public EvalFunc(String inputName, Serializable compiledExpression) {
            this.inputName = inputName;
            this.compiledExpression = compiledExpression;
        }

        public EvalFunc(String inputName) {
            this(inputName, null);
        }

        @Override
        public Set<String> getResourceNames() {
            return Set.of("expression");
        }

        @Override
        public void bindResources(Map<String, Object> resources) {
            Object obj = resources.get("expression");
            if (!(obj instanceof String)) {
                throw new IllegalArgumentException("No 'expression', MVEL2 expression in text, is available in given resources");
            }
            String expression = (String) obj;
            this.compiledExpression = MVEL.compileExpression(expression);
        }

        @Override
        public Object apply(Object o) {
            Map<String, Object> variables = Map.of(inputName, o);
            return MVEL.executeExpression(compiledExpression, variables);
        }
    }

    private Func createEvalFunc(TreeNode spec) {
        final String inputName = spec.getDataOfChildAt(0);
        final String exprText = spec.getDataOfChildAt(1);
        try {
            if (inputName == null) {
                throw new IllegalArgumentException("'inputName' cannot be null");
            }

            if (exprText == null) {
                throw new IllegalArgumentException("'expression' cannot be null");
            }

            final String expression = FuncHelper.decodeIfEncoded(exprText);
            Serializable compiledExpression = MVEL.compileExpression(expression);
            return new EvalFunc(inputName, compiledExpression);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "MVEL2:EVAL(inputName,expression)",
                    "MVEL2:EVAL(user,user.firstName)",
                    e
            );
        }
    }

    private Func createEval1Func(TreeNode spec) {
        final String inputName = spec.getDataOfChildAt(0);
        final String expressionId = spec.getDataOfChildAt(1);
        final String resourceRegistryId = spec.getDataOfChildAt(2, ResourceRegistry.class.getSimpleName());

        try {
            if (inputName == null) {
                throw new IllegalArgumentException("'inputName' cannot be null");
            }

            if (expressionId == null || expressionId.isEmpty()) {
                return new EvalFunc(inputName);
            }

            ResourceRegistry resourceRegistry = getRequiredDependency(resourceRegistryId, ResourceRegistry.class);
            String expression = resourceRegistry.get(expressionId);
            if (expression == null || expression.isEmpty()) {
                throw new IllegalStateException(String.format("MVEL2 expression with id '%s' is not available", expressionId));
            }
            Serializable compiledExpression = MVEL.compileExpression(expression);
            return new EvalFunc(inputName, compiledExpression);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "MVEL2:EVAL1(inputName,expressionId,resourceRegistryId)",
                    "MVEL2:EVAL1(user,fn.mvel)",
                    e
            );
        }
    }

}
