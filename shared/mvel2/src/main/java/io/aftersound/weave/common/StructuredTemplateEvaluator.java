package io.aftersound.weave.common;

import io.aftersound.weave.mvel2.CompiledTemplateRegistry;
import org.mvel2.CompileException;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An evaluator for {@link SimpleStructuredTemplate}
 */
public final class StructuredTemplateEvaluator {

    private final TemplateEvaluator templateEvaluator;

    public StructuredTemplateEvaluator(CompiledTemplateRegistry compiledTemplateRegistry) {
        this(new TemplateEvaluator(compiledTemplateRegistry));
    }

    public StructuredTemplateEvaluator(TemplateEvaluator templateEvaluator) {
        this.templateEvaluator = templateEvaluator;
    }

    public String evaluate(StructuredTemplate structuredTemplate, Map<String, Object> variables) {
        Map<String, Object> vars = new LinkedHashMap<>();

        if (structuredTemplate.getOptionalVariables() != null) {
            // make sure required variables exist in variables map for evaluation
            for (String requiredVariable : structuredTemplate.getOptionalVariables()) {
                vars.put(requiredVariable, null);
            }
        }

        vars.putAll(variables);

        if (structuredTemplate instanceof SimpleStructuredTemplate) {
            return _evaluate((SimpleStructuredTemplate) structuredTemplate, vars);
        } else if (structuredTemplate instanceof MultiSelectionStructuredTemplate) {
            return _evaluate((MultiSelectionStructuredTemplate) structuredTemplate, vars);
        } else {
            throw new CompileException("Unsupported StructuredTemplate", new char[0], -1);
        }
    }

    private String _evaluate(SimpleStructuredTemplate simpleStructuredTemplate, Map<String, Object> variables) {
        Map<String, String> evaluatedElements = new HashMap<>();
        for (Map.Entry<String, String> e : simpleStructuredTemplate.getElements().entrySet()) {
            String name = e.getKey();
            String evaluated = templateEvaluator.evaluate(e.getValue(), variables);
            evaluatedElements.put(name, evaluated);
        }
        variables.putAll(evaluatedElements);

        return templateEvaluator.evaluate(simpleStructuredTemplate.getTemplate(), variables);
    }

    /**
     * Evaluate {@link MultiSelectionStructuredTemplate} with given map of variables
     * @param structuredTemplate
     *          a {@link MultiSelectionStructuredTemplate}
     * @param variables
     *          variables which might be referred to by the template and element templates
     * @return
     *          evaluated result
     */
    private String _evaluate(MultiSelectionStructuredTemplate structuredTemplate, Map<String, Object> variables) {
        MultiSelectionEvaluator selectionEvaluator = new MultiSelectionEvaluator(templateEvaluator);
        Map<String, String> namedElements = selectionEvaluator.evaluate(structuredTemplate.getElements(), variables);
        variables.putAll(namedElements);

        return templateEvaluator.evaluate(structuredTemplate.getTemplate(), variables);
    }

}
