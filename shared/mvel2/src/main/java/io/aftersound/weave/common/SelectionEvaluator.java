package io.aftersound.weave.common;

import io.aftersound.weave.mvel2.CompiledTemplateRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluator which evaluates {@link Selection}s
 */
public final class SelectionEvaluator {

    private final TemplateEvaluator templateEvaluator;

    public SelectionEvaluator(CompiledTemplateRegistry compiledTemplateRegistry) {
        this.templateEvaluator = new TemplateEvaluator(compiledTemplateRegistry);
    }

    public SelectionEvaluator(TemplateEvaluator templateEvaluator) {
        this.templateEvaluator = templateEvaluator;
    }

    /**
     * Evaluate named {@link Selection}s
     * @param namedSelections
     *          named {@link Selection}s. Key is name, value is {@link Selection}
     * @param variables
     *          variables used to evaluate {@link Selection} and choices within
     * @return
     *          selected choices associated with names
     */
    public Map<String, String> evaluateSelections(Map<String, Selection> namedSelections, Map<String, Object> variables) {
        if (namedSelections == null || namedSelections.isEmpty()) {
            return Collections.EMPTY_MAP;
        }

        Map<String, String> selectedChoices = new HashMap<>();
        for (Map.Entry<String, Selection> e : namedSelections.entrySet()) {
            Selection selection = e.getValue();
            String selected = evaluateSelection(selection, variables);
            if (selected != null) {
                selectedChoices.put(e.getKey(), selected);
            }
        }
        return selectedChoices;
    }

    /**
     * Evaluate given {@link Selection} and return selected choice
     * @param selection
     *          a {@link Selection}
     * @param variables
     *          variables used to evaluate {@link Selection} and choices within
     * @return selected choice
     */
    public String evaluateSelection(Selection selection, Map<String, Object> variables) {
        if (!isValid(selection)) {
            return null;
        }

        // evaluate selector to determine id of choice
        String choiceId = templateEvaluator.evaluate(selection.getSelector(), variables);

        // get choice expression
        String choice = selection.getChoices().get(choiceId);
        return templateEvaluator.evaluate(choice, variables);
    }

    private static boolean isValid(Selection selection) {
        if (selection == null) {
            return false;
        }

        if (selection.getSelector() == null || selection.getSelector().isEmpty()) {
            return false;
        }

        if (selection.getChoices() == null || selection.getChoices().isEmpty()) {
            return false;
        }

        return true;
    }
}
