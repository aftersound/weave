package io.aftersound.mvel2;

import java.util.List;
import java.util.Map;

/**
 * A structured compound template which has a main template,
 * which references the evaluated results of elements during its
 * evaluation.
 */
public abstract class StructuredTemplate {

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
     * of evaluated results of elements
     */
    private String template;

    /**
     * A set of elements, each is also a template expression with
     * a name, effectively the key in the map
     */
    private Map<String, String> elements;

    public final List<String> getOptionalVariables() {
        return optionalVariables;
    }

    public final void setOptionalVariables(List<String> optionalVariables) {
        this.optionalVariables = optionalVariables;
    }

    public final String getTemplate() {
        return template;
    }

    public final void setTemplate(String template) {
        this.template = template;
    }

}
