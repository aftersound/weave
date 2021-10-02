package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public abstract class ConfigProvider<CONFIG>{

    protected ObjectMapper configReader;

    protected void setConfigReader(ObjectMapper configReader) {
        this.configReader = configReader;
    }

    protected abstract List<CONFIG> getConfigList();

}
