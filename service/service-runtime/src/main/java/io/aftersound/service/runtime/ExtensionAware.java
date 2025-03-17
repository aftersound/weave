package io.aftersound.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class ExtensionAware {

    protected ObjectMapper objectMapper;

    /**
     * @param objectMapper an {@link ObjectMapper} that is aware of extensions' control/config types
     */
    protected final void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

}
