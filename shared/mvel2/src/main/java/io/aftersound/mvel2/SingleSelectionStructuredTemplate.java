package io.aftersound.mvel2;

/**
 * A structured compound template which might consist of elements
 */
public final class SingleSelectionStructuredTemplate extends StructuredTemplate {

    /**
     * A selection of element templates based on selector, which
     * is also a template expression
     */
    private SingleSelection elements;

    public SingleSelection getElements() {
        return elements;
    }

    public void setElements(SingleSelection elements) {
        this.elements = elements;
    }
}
