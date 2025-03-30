package io.aftersound.func.groovy;

import groovy.lang.Binding;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.aftersound.func.*;
import io.aftersound.util.ResourceRegistry;
import io.aftersound.util.TreeNode;

import java.util.Map;
import java.util.Set;

import static io.aftersound.func.FuncHelper.getRequiredDependency;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GroovyFuncFactory extends MasterAwareFuncFactory {

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("GROOVY:EVAL".equals(funcName)) {
            return createEvalFunc(spec);
        }

        if ("GROOVY:EVAL1".equals(funcName)) {
            return createEval1Func(spec);
        }

        return null;
    }

    static class EvalFunc extends AbstractFuncWithHints<Object, Object> implements ResourceAware {

        private final String inputName;
        private Script script;

        public EvalFunc(String inputName) {
            this(inputName, null);
        }

        public EvalFunc(String inputName, Script script) {
            this.inputName = inputName;
            this.script = script;
        }

        @Override
        public Set<String> getResourceNames() {
            return Set.of("script");
        }

        @Override
        public void bindResources(Map<String, Object> resources) {
            Object obj = resources.get("script");
            if (!(obj instanceof String)) {
                throw new IllegalArgumentException("No 'script', Groovy script in text, is available in given resources");
            }
            String scriptText = (String) obj;
            String decodedScriptText = FuncHelper.decodeIfEncoded(scriptText);
            this.script = new GroovyShell().parse(decodedScriptText);
        }

        @Override
        public Object apply(Object input) {
            try {
                Script scriptInstance = script.getClass().getDeclaredConstructor().newInstance();

                Binding binding = new Binding();
                binding.setVariable(inputName, input);
                scriptInstance.setBinding(binding);

                Object result = scriptInstance.run();
                if (result instanceof GString) {
                    return ((GString) result).toString();
                } else {
                    return result;
                }
            } catch (Exception e) {
                throw new ExecutionException("Failed to apply/evaluate Groovy script on given input", e);
            }
        }
    }

    private Func createEvalFunc(TreeNode spec) {
        final String inputName = spec.getDataOfChildAt(0);
        final String scriptText = spec.getDataOfChildAt(1);

        try {
            if (inputName == null) {
                throw new IllegalArgumentException("'inputName' cannot be null");
            }
            if (scriptText == null) {
                throw new IllegalArgumentException("'script' cannot be null");
            }
            String decodedScriptText = FuncHelper.decodeIfEncoded(scriptText);
            Script script = new GroovyShell().parse(decodedScriptText);
            return new EvalFunc(inputName, script);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "GROOVY:EVAL(inputName,script) or GROOVY:EVAL(inputName,BASE64|base64EncodedScript) or GROOVY:EVAL(inputName,URL|urlEncodedScript)",
                    "GROOVY:EVAL(user,user.firstName) or GROOVY:EVAL(inputName,URL|user.getFirstName%28%29)",
                    e
            );
        }
    }

    private Func createEval1Func(TreeNode spec) {
        final String inputName = spec.getDataOfChildAt(0);
        final String scriptId = spec.getDataOfChildAt(1);
        final String resourceRegistryId = spec.getDataOfChildAt(2, ResourceRegistry.class.getSimpleName());

        try {
            if (inputName == null) {
                throw new IllegalArgumentException("'inputName' cannot be null");
            }

            if (scriptId == null || scriptId.isEmpty()) {
                return new EvalFunc(inputName);
            }

            ResourceRegistry resourceRegistry = getRequiredDependency(resourceRegistryId, ResourceRegistry.class);
            String scriptText = resourceRegistry.get(scriptId);
            if (scriptText == null) {
                throw new IllegalStateException("No script resource for script with id as " + scriptId);
            }

            String decodedScriptText = FuncHelper.decodeIfEncoded(scriptText);
            Script script = new GroovyShell().parse(decodedScriptText);
            return new EvalFunc(inputName, script);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "GROOVY:EVAL1(inputName) or GROOVY:EVAL1(inputName,scriptId) or GROOVY:EVAL1(inputName,scriptId,scriptResourceRegistryId)",
                    "GROOVY:EVAL1(user)",
                    e
            );
        }
    }

}
