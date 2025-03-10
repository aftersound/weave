package io.aftersound.func.common;

import groovy.lang.Binding;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.aftersound.func.*;
import io.aftersound.util.ResourceRegistry;
import io.aftersound.util.TreeNode;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.aftersound.func.FuncHelper.getRequiredDependency;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GroovyFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = DescriptorHelper.getDescriptors(GroovyFuncFactory.class);

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("GROOVY:EVAL".equals(funcName)) {
            return createEvalFunc(spec);
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
            this.script = new GroovyShell().parse(scriptText);
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
        final String scriptId = spec.getDataOfChildAt(1);
        final String resourceRegistryId = spec.getDataOfChildAt(2, ResourceRegistry.class.getSimpleName());

        if (inputName == null || inputName.isEmpty()) {
            throw FuncHelper.createCreationException(
                    spec,
                    "GROOVY:EVAL(inputName) or GROOVY:EVAL(inputName,scriptId) or GROOVY:EVAL(inputName,scriptId,scriptResourceRegistryId)",
                    "GROOVY:EVAL(user)"
            );
        }

        if (scriptId == null || scriptId.isEmpty()) {
            return new EvalFunc(inputName);
        }

        try {
            ResourceRegistry resourceRegistry = getRequiredDependency(resourceRegistryId, ResourceRegistry.class);
            String scriptText = resourceRegistry.get(scriptId);
            Script script = new GroovyShell().parse(scriptText);
            return new EvalFunc(inputName, script);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "GROOVY:EVAL(inputName) or GROOVY:EVAL(inputName,scriptId) or GROOVY:EVAL(inputName,scriptId,scriptResourceRegistryId)",
                    "GROOVY:EVAL(user)",
                    e
            );
        }
    }

}
