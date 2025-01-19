package io.aftersound.util;

import java.util.Map;

public interface HintHolder {

    /**
     * Add given hint into this holder
     *
     * @param id - the id of the hint to be added
     * @param value - the value of the hint to be added
     */
    void addHint(String id, String value);

    /**
     * Add all given hints into this holder
     *
     * @param hints - the hints to be added
     */
    void addAll(Map<String, String> hints);

    /**
     * Returns true if this holds the specified hint
     *
     * @param id - the id of the hint whose presence is to be tested
     * @param value - the value of the hint whose presence is to be tested
     * @return true if this holds the specified hint
     */
    boolean hasHint(String id, String value);

    /**
     * Lock this hint holder. Once locked, the hints in this holder
     * cannot be changed
     */
    void lockHints();

}
