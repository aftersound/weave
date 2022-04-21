package io.aftersound.weave.service.discovery;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

import java.util.List;

public class ServiceMetadataExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "ServiceMetadataService",
            ServiceMetadataExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private List<String> serviceMetadataRegistries;

    public List<String> getServiceMetadataRegistries() {
        return serviceMetadataRegistries;
    }

    public void setServiceMetadataRegistries(List<String> serviceMetadataRegistries) {
        this.serviceMetadataRegistries = serviceMetadataRegistries;
    }
}
