package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.service.SpecExtractor;
import io.aftersound.weave.service.metadata.ServiceMetadata;

import java.util.ArrayList;
import java.util.List;

public abstract class ServiceMetadataProvider {

    protected ObjectMapper configReader;

    protected final void setConfigReader(ObjectMapper configReader) {
        this.configReader = configReader;
    }

    protected final List<ServiceMetadata> configs() {
        List<ServiceMetadata> serviceMetadataList = new ArrayList<>(getConfigList());
        serviceMetadataList.addAll(EmbeddedRuntimeConfig.getServiceMetadataList());
        return serviceMetadataList;
    }

    protected abstract <SPEC> SPEC getSpec(SpecExtractor<SPEC> specExtractor);

    protected abstract List<ServiceMetadata> getConfigList();

}
