package io.aftersound.weave.common;

import java.io.Serializable;
import java.util.Map;

/**
 * A conceptual function which acts on input and produces output
 *
 * @param <S> generic type of source/input value
 * @param <T> generic type of target/output value
 */
public abstract class ValueFunc<S,T> implements Serializable {

    private Map<String, String> hints;

    /**
     * Optional resources or dependencies
     */
    protected transient Resources resources;

    public final void setHints(Map<String, String> hints) {
        this.hints = hints;
    }

    /**
     * @param id the hint id
     * @param value the expected hint value
     * @return true of this func has specified hint (id, value)
     */
    public final boolean hasHint(String id, String value) {
        return hints != null && value.equals(hints.get(id));
    }

    /**
     * @param source source input
     * @return the output produced by this func applied on given source input
     */
    public abstract T apply(S source);

}
