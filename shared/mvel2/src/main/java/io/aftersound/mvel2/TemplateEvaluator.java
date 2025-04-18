package io.aftersound.mvel2;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import java.util.Map;

/**
 * Evaluator that evaluates MVEL2 template in string
 */
public final class TemplateEvaluator {

    private final CompiledTemplateRegistry compiledTemplateRegistry;

    public TemplateEvaluator(CompiledTemplateRegistry compiledTemplateRegistry) {
        this.compiledTemplateRegistry = compiledTemplateRegistry;
    }

    /**
     * Evaluate MVEL2 template with available variables
     * @param template
     *          an MVEL2 template
     * @param variables
     *          variables used to evaluate specified template
     * @return
     *          evaluated result
     */
    public String evaluate(String template, Map<String, Object> variables) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        CompiledTemplate compiledTemplate = compiledTemplateRegistry.getCompiledTemplate(template);
        Object rendered = TemplateRuntime.execute(compiledTemplate, variables);
        return (rendered != null ? rendered.toString() : null);
    }
}
