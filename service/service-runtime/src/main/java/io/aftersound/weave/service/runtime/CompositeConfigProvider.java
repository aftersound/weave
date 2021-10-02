package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class CompositeConfigProvider<CONFIG> extends ConfigProvider<CONFIG> {

    private final List<ConfigProvider<CONFIG>> configProviders;

    CompositeConfigProvider(ConfigProvider<CONFIG>... configProviders) {
        if (configProviders != null) {
            this.configProviders = Arrays.asList(configProviders);
        } else {
            this.configProviders = Collections.emptyList();
        }
    }

    @Override
    protected void setConfigReader(ObjectMapper configReader) {
        configProviders.forEach(cp -> cp.setConfigReader(configReader));
    }

    @Override
    protected List<CONFIG> getConfigList() {
        List<CONFIG> combinedConfigList = new ArrayList<>();
        for (ConfigProvider<CONFIG> cp : configProviders) {
            List<CONFIG> cl = cp.getConfigList();
            if (cl != null) {
                combinedConfigList.addAll(cp.getConfigList());
            }
        }
        return combinedConfigList;
    }

}
