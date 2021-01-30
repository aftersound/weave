package io.aftersound.weave.common;

/**
 * A structured compound template which might consist of elements
 */
public final class MultiSelectionStructuredTemplate extends StructuredTemplate {

    /**
     * A selection of element templates based on selector, which
     * is also a template expression
     */
    private MultiSelection elements;

    public MultiSelection getElements() {
        return elements;
    }

    public void setElements(MultiSelection elements) {
        this.elements = elements;
    }
}
