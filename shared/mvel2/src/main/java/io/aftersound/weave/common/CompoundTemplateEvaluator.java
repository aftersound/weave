package io.aftersound.weave.common;

import io.aftersound.weave.mvel2.CompiledTemplateRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An evaluator for {@link CompoundTemplate}
 */
public final class CompoundTemplateEvaluator {

    private final TemplateEvaluator templateEvaluator;

    public CompoundTemplateEvaluator(CompiledTemplateRegistry compiledTemplateRegistry) {
        this(new TemplateEvaluator(compiledTemplateRegistry));
    }

    public CompoundTemplateEvaluator(TemplateEvaluator templateEvaluator) {
        this.templateEvaluator = templateEvaluator;
    }

    /**
     * Evaluate {@link CompoundTemplate} with given map of variables
     * @param compoundTemplate
     *          a {@link CompoundTemplate}
     * @param variables
     *          variables which might be referred to by the template and element templates
     * @return
     *          evaluated result
     */
    public String evaluate(CompoundTemplate compoundTemplate, Map<String, Object> variables) {
        Map<String, Object> vars = new LinkedHashMap<>();

        if (compoundTemplate.getOptionalVariables() != null) {
            // make sure required variables exist in variables map for evaluation
            for (String requiredVariable : compoundTemplate.getOptionalVariables()) {
                vars.put(requiredVariable, null);
            }
        }

        vars.putAll(variables);

        MultiSelectionEvaluator selectionEvaluator = new MultiSelectionEvaluator(templateEvaluator);
        Map<String, String> namedElements = selectionEvaluator.evaluate(compoundTemplate.getElements(), vars);
        vars.putAll(namedElements);

        return templateEvaluator.evaluate(compoundTemplate.getTemplate(), vars);
    }
}
