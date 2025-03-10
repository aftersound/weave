package io.aftersound.weave.sample.extension.service;

import io.aftersound.util.MapBuilder;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.Util;
import io.aftersound.weave.service.request.ParamValueHolders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class GreetingServiceExecutor extends ServiceExecutor<Map<String, String>> {

    public static final NamedType<ExecutionControl> COMPANION_CONTROL_TYPE = GreetingExecutionControl.TYPE;

    private static final Logger LOGGER = LoggerFactory.getLogger(GreetingServiceExecutor.class);

    public GreetingServiceExecutor(ComponentRepository componentRepository) {
        super(componentRepository);
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Map<String, String> execute(ExecutionControl executionControl, ParamValueHolders request, ServiceContext context) {
        GreetingExecutionControl ec = Util.safeCast(executionControl, GreetingExecutionControl.class);
        if (ec == null) {
            LOGGER.error("Given ExecutionControl is not instance of {}", GreetingExecutionControl.class.getName());
            context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error("ExecutionControl is missing or malformed"));
            return null;
        }

        String name = request.firstWithName("name").singleValue(String.class);
        return MapBuilder.<String, String>hashMap()
                .put("greeting", selectGreetingWord(ec.getGreetingWords()) + "," + name)
                .build();
    }

    private String selectGreetingWord(List<String> choices) {
        if (choices == null || choices.isEmpty()) {
            return "Hello";
        }
        int index = ((int)(Math.random() * 100)) % choices.size();
        return choices.get(index);
    }
}
