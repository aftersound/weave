package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public abstract class ConfigProvider<CONFIG>{

    protected ObjectMapper configReader;

    public final void setConfigReader(ObjectMapper configReader) {
        this.configReader = configReader;
    }

    public abstract List<CONFIG> getConfigList();

}
