package io.aftersound.service.metadata.param;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum ParamType {
    Method,
    Header,
    Path,
    Query,
    Body,

    /**
     * Derived parameter could be derived from Header/Path/Query/Body parameter
     */
    Derived,

    /**
     * Predefined parameter is expected to be required.
     */
    Predefined;

    private static final Map<String, ParamType> LOOKUP;
    static {
        Map<String, ParamType> lookup = new HashMap<>();
        Arrays.stream(ParamType.values()).forEach(
                pt -> lookup.put(pt.name().toLowerCase(), pt)
        );
        LOOKUP = Collections.unmodifiableMap(lookup);
    }

    public static ParamType from(String name) {
        return LOOKUP.get(name.toLowerCase());
    }
}
