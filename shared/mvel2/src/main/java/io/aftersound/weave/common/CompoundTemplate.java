package io.aftersound.weave.common;

import java.util.List;

/**
 * A structured compound template which might consist of elements
 */
public class CompoundTemplate {

    /**
     * List of variables which might not show up in variables
     * map for template evaluation. However, in order for
     * evaluation to succeed, there must be null entries in
     * variables map, or else evaluation will fail because how
     * {@link org.mvel2.templates.TemplateRuntime} works inside.
     */
    private List<String> optionalVariables;

    /**
     * Main template expression which might refers to/consists
     * of elements
     */
    private String template;

    /**
     * A selection of element templates based on selector, which
     * is also a template expression
     */
    private MultiSelection elements;

    public List<String> getOptionalVariables() {
        return optionalVariables;
    }

    public void setOptionalVariables(List<String> optionalVariables) {
        this.optionalVariables = optionalVariables;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public MultiSelection getElements() {
        return elements;
    }

    public void setElements(MultiSelection elements) {
        this.elements = elements;
    }
}
