package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.component.ComponentConfig;

import java.util.ArrayList;
import java.util.List;

public abstract class ComponentConfigProvider {

    protected ObjectMapper configReader;

    protected final void setConfigReader(ObjectMapper configReader) {
        this.configReader = configReader;
    }

    protected final List<ComponentConfig> configs() {
        List<ComponentConfig> componentConfigList = new ArrayList<>(getConfigList());
        componentConfigList.addAll(EmbeddedRuntimeConfig.getComponentConfigList());
        return componentConfigList;
    }

    protected abstract List<ComponentConfig> getConfigList();

}
