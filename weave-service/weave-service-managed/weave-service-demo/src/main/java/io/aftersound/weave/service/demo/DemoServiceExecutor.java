package io.aftersound.weave.service.demo;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.service.resources.ManagedResources;

import java.util.LinkedHashMap;
import java.util.Map;

public class DemoServiceExecutor extends ServiceExecutor {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = DemoServiceExecutionControl.TYPE;

    public DemoServiceExecutor(ManagedResources managedResources) {
        super(managedResources);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Object execute(ServiceMetadata serviceMetadata, ParamValueHolders request, ServiceContext context) {
        Map<String, Object> response = new LinkedHashMap<>();
        for (ParamValueHolder pvh : request.all()) {
            if (pvh.metadata().isMultiValued()) {
                response.put(pvh.getParamName(), pvh.multiValues(String.class));
            } else {
                response.put(pvh.getParamName(), pvh.singleValue(String.class));
            }
        }
        return response;
    }
}
