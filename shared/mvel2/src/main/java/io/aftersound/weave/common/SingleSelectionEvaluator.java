package io.aftersound.weave.common;

import io.aftersound.weave.mvel2.CompiledTemplateRegistry;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluator which evaluates {@link SingleSelection}s
 */
public final class SingleSelectionEvaluator {

    private final TemplateEvaluator templateEvaluator;

    public SingleSelectionEvaluator(CompiledTemplateRegistry compiledTemplateRegistry) {
        this.templateEvaluator = new TemplateEvaluator(compiledTemplateRegistry);
    }

    public SingleSelectionEvaluator(TemplateEvaluator templateEvaluator) {
        this.templateEvaluator = templateEvaluator;
    }

    /**
     * Evaluate named {@link SingleSelection}s
     * @param namedSelections
     *          named {@link SingleSelection}s. Key is name, value is {@link SingleSelection}
     * @param variables
     *          variables used to evaluate {@link SingleSelection} and choices within
     * @return
     *          selected choices associated with names
     */
    public Map<String, String> evaluateSelections(Map<String, SingleSelection> namedSelections, Map<String, Object> variables) {
        if (namedSelections == null || namedSelections.isEmpty()) {
            return Collections.EMPTY_MAP;
        }

        Map<String, String> selectedChoices = new HashMap<>();
        for (Map.Entry<String, SingleSelection> e : namedSelections.entrySet()) {
            SingleSelection selection = e.getValue();
            String selected = evaluateSelection(selection, variables);
            if (selected != null) {
                selectedChoices.put(e.getKey(), selected);
            }
        }
        return selectedChoices;
    }

    /**
     * Evaluate given {@link SingleSelection} and return selected choice
     * @param selection
     *          a {@link SingleSelection}
     * @param variables
     *          variables used to evaluate {@link SingleSelection} and choices within
     * @return selected choice
     */
    public String evaluateSelection(SingleSelection selection, Map<String, Object> variables) {
        if (!isValid(selection)) {
            return null;
        }

        // evaluate selector to determine id of choice
        String choiceId = templateEvaluator.evaluate(selection.getSelector(), variables);

        // get choice expression
        String choice = selection.getChoices().get(choiceId);
        return templateEvaluator.evaluate(choice, variables);
    }

    private static boolean isValid(SingleSelection selection) {
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
