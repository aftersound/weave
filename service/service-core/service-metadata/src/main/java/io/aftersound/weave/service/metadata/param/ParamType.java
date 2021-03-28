package io.aftersound.weave.service.metadata.param;

public enum ParamType {
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
    Predefined
}
