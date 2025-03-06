package io.aftersound.func.common;

import groovy.lang.Binding;
import groovy.lang.GString;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.aftersound.func.*;
import io.aftersound.schema.Constraint;
import io.aftersound.schema.Field;
import io.aftersound.schema.Type;
import io.aftersound.util.ResourceRegistry;
import io.aftersound.util.TreeNode;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.aftersound.func.FuncHelper.getRequiredDependency;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GroovyFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Collections.singletonList(
            Descriptor.builder("GROOVY:EVAL")
                    .withDescription("Evaluate and execute a Groovy script on the input")
                    .withControls(
                            Field.stringFieldBuilder("scriptId")
                                    .withConstraint(Constraint.optional())
                                    .withDescription("The id of the Groovy script to execute, stored in the ResourceRegistry. When missing, Groovy script is expected to be provided through resource binding mechanism.")
                                    .build(),
                            Field.stringFieldBuilder("resourceRegistryId")
                                    .withConstraint(Constraint.optional())
                                    .withDescription("The id of the resource registry where the script is stored")
                                    .build()
                    )
                    .withInput(
                            Field.builder("input", Type.builder("Varies").build())
                                    .withConstraint(Constraint.required())
                                    .withDescription("The input data provided to the script")
                                    .build()
                    )
                    .withOutput(
                            Field.builder("output", Type.builder("Varies").build())
                                    .withDescription("The output returned by the script execution")
                                    .build()
                    )
                    .build()

    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return super.getFuncDescriptors();
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

        private Script script;

        public EvalFunc(Script script) {
            this.script = script;
        }

        @Override
        public Object apply(Object input) {
            try {
                Script scriptInstance = script.getClass().getDeclaredConstructor().newInstance();

                Binding binding = new Binding();
                binding.setVariable("input", input);
                scriptInstance.setBinding(binding);

                Object result = scriptInstance.run();
                if (result instanceof GString) {
                    return ((GString) result).toString();
                } else {
                    return result;
                }
            } catch (Exception e) {
                throw new ExecutionException("", e);
            }
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
    }

    private Func createEvalFunc(TreeNode spec) {
        final String scriptId = spec.getDataOfChildAt(0);
        if (scriptId == null || scriptId.isEmpty()) {
            return new EvalFunc(null);
        }

        final String resourceRegistryId = spec.getDataOfChildAt(1, ResourceRegistry.class.getSimpleName());
        try {
            ResourceRegistry resourceRegistry = getRequiredDependency(resourceRegistryId, ResourceRegistry.class);
            String scriptText = resourceRegistry.get(scriptId);
            Script script = new GroovyShell().parse(scriptText);
            return new EvalFunc(script);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "GROOVY:EVAL() or GROOVY:EVAL(scriptId) or GROOVY:EVAL(scriptId,resourceRegistryId)",
                    "GROOVY:EVAL(transform)",
                    e
            );
        }
    }

}
