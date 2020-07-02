package io.aftersound.weave.common;

import java.util.Map;

/**
 * Conceptual entity represents selection out of choices
 */
public class Selection {

    /**
     * a selector in MVEL2 expression, when evaluated, it
     * will generate a string value represents the name
     * of selected choice
     */
    private String selector;

    /**
     * a set of choices in form of map, each entry's key
     * is the name of corresponding choice, and its value
     * is an expression of MVEL2, which evaluated, it yield
     * finalize choice
     */
    private Map<String, String> choices;

    public String getSelector() {
        return selector;
    }

    public void setSelector(String selector) {
        this.selector = selector;
    }

    public Map<String, String> getChoices() {
        return choices;
    }

    public void setChoices(Map<String, String> choices) {
        this.choices = choices;
    }
}
