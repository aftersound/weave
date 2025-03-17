package io.aftersound.mvel2;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Evaluator which evaluates {@link MultiSelection}
 */
public final class MultiSelectionEvaluator {

    private final TemplateEvaluator templateEvaluator;

    public MultiSelectionEvaluator(CompiledTemplateRegistry compiledTemplateRegistry) {
        this(new TemplateEvaluator(compiledTemplateRegistry));
    }

    public MultiSelectionEvaluator(TemplateEvaluator templateEvaluator) {
        this.templateEvaluator = templateEvaluator;
    }

    /**
     * Evaluate given {@link MultiSelection} and return selected choices
     * @param selection
     *          a {@link MultiSelection}
     * @param variables
     *          variables used to evaluate {@link MultiSelection} and choices within
     * @return selected choices
     */
    public Map<String, String> evaluate(MultiSelection selection, Map<String, Object> variables) {
        if (!isValid(selection)) {
            return Collections.EMPTY_MAP;
        }

        // evaluate selector to determine id of choice
        String choiceId = templateEvaluator.evaluate(selection.getSelector(), variables);

        // get choice expressions
        Map<String, String> choices = selection.getChoices().get(choiceId);
        if (choices == null || choices.isEmpty()) {
            return Collections.EMPTY_MAP;
        }

        Map<String, String> evaluated = new HashMap<>();
        for (Map.Entry<String, String> e : choices.entrySet()) {
            String id = e.getKey();
            String choiceValue = templateEvaluator.evaluate(e.getValue(), variables);
            evaluated.put(id, choiceValue);
        }
        return evaluated;
    }

    private static boolean isValid(MultiSelection selection) {
        if (selection == null) {
            return false;
        }

        if (selection.getSelector() == null) {
            return false;
        }

        if (selection.getChoices() == null || selection.getChoices().isEmpty()) {
            return false;
        }

        return true;
    }

}
