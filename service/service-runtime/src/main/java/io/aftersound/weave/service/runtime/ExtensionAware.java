package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ExtensionAware {

    protected ObjectMapper configReader;

    /**
     * @param configReader an {@link ObjectMapper} that is aware of extensions' control types
     */
    protected final void setConfigReader(ObjectMapper configReader) {
        this.configReader = configReader;
    }

}
