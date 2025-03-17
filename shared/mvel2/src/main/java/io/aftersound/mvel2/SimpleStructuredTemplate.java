package io.aftersound.mvel2;

import java.util.Map;

/**
 * A structured compound template which has a main template,
 * which references the evaluated results of elements during its
 * evaluation.
 */
public final class SimpleStructuredTemplate extends StructuredTemplate {

    /**
     * A set of elements, each is also a template expression with
     * a name, effectively the key in the map
     */
    private Map<String, String> elements;

    public Map<String, String> getElements() {
        return elements;
    }

    public void setElements(Map<String, String> elements) {
        this.elements = elements;
    }
}
