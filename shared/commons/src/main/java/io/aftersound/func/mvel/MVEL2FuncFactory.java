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
                return createEvalFunc1(spec);
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

    static class EvalFunc1 extends AbstractFuncWithHints<Map<String, Object>, Object> {

        private final Serializable compiledExpression;

        public EvalFunc1(Serializable compiledExpression) {
            this.compiledExpression = compiledExpression;
        }

        @Override
        public Object apply(Map<String, Object> variables) {
            if (variables != null) {
                return MVEL.executeExpression(compiledExpression, variables);
            } else {
                return null;
            }
        }

    }

    private Func createEvalFunc(TreeNode spec) {
        final String inputName = spec.getDataOfChildAt(0);
        final String expressionId = spec.getDataOfChildAt(1);
        final String resourceRegistryId = spec.getDataOfChildAt(2, ResourceRegistry.class.getSimpleName());

        if (inputName == null || inputName.isEmpty()) {
            throw FuncHelper.createCreationException(
                    spec,
                    "MVEL2:EVAL(inputName) or MVEL2:EVAL(inputName,expressionId) or MVEL2:EVAL(inputName,expressionId,resourceRegistryId)",
                    "MVEL2:EVAL(user)"
            );
        }

        if (expressionId == null || expressionId.isEmpty()) {
            return new EvalFunc(inputName);
        }

        try {
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
                    "MVEL2:EVAL(inputName,expressionId,resourceRegistryId)",
                    "MVEL2:EVAL(user,fn.mvel)",
                    e
            );
        }
    }

    private Func createEvalFunc1(TreeNode spec) {
        try {
            final String expression = URLDecoder.decode(spec.getChildAt(0).toString(), StandardCharsets.UTF_8);
            Serializable compiledExpression = MVEL.compileExpression(expression);
            return new EvalFunc1(compiledExpression);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "MVEL2:EVAL1(expression)",
                    "MVEL2:EVAL1(user.firstName)",
                    e
            );
        }
    }

}
