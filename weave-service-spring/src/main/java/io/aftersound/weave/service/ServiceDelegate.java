package io.aftersound.weave.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.message.MessageData;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.message.Severity;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.request.*;
import io.aftersound.weave.service.response.ServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * This {@link ServiceDelegate} is called by service controller to
 * serve request(s).
 */
class ServiceDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDelegate.class);
    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private final ServiceMetadataManager serviceMetadataManager;
    private final ServiceExecutorFactory serviceExecutorFactory;
    private final ActorFactory<DeriveControl, Deriver, ParamValueHolder> paramDeriverFactory;
    private final CacheRegistry cacheRegistry;

    ServiceDelegate(
            ServiceMetadataManager serviceMetadataManager,
            ServiceExecutorFactory serviceExecutorFactory,
            ActorFactory<DeriveControl, Deriver, ParamValueHolder> paramDeriverFactory,
            CacheRegistry cacheRegistry) {
        this.serviceMetadataManager = serviceMetadataManager;
        this.serviceExecutorFactory = serviceExecutorFactory;
        this.paramDeriverFactory = paramDeriverFactory;
        this.cacheRegistry = cacheRegistry;
    }

    /**
     * Each request is served roughly as below
     *  1.identify {@link ServiceMetadata} based on request URI
     *  2.identify {@link ServiceExecutor} in according to identified {@link ServiceMetadata}
     *  3.parse and validate request based on param fields defined in {@link ServiceMetadata}
     *  4.invoke identified {@link ServiceExecutor} with parsed parameters
     *  5.wrap and return response from {@link ServiceExecutor} invocation
     *
     * @param request
     *          a {@link HttpServletRequest}
     * @return
     *          an object represents service response. Type of response object is determined
     *          by {@link ServiceExecutor} in context
     */
    Object serve(HttpServletRequest request) {
        ServiceContext context = new ServiceContext();

        // 1.identify ServiceMetadata
        ServiceMetadata serviceMetadata = serviceMetadataManager.getServiceMetadata(request.getRequestURI());

        // 1.1.validate
        if (serviceMetadata == null) {
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setMessages(Collections.singletonList(MessageRegistry.NO_RESOURCE.error(request.getRequestURI())));
            return serviceResponse;
        }

        // 2.identify ServiceExecutor
        ServiceExecutor serviceExecutor = serviceExecutorFactory.getServiceExecutor(serviceMetadata);

        // 2.1.validate
        if (serviceExecutor == null) {
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setMessages(Collections.singletonList(MessageRegistry.NO_SERVICE_EXECUTOR.error(request.getRequestURI())));
            return serviceResponse;
        }

        // 3.Process and extract request parameters
        ParamValueHolders paramValueHolders = new RequestProcessor<>(
                serviceMetadata,
                new CoreParameterProcessor(paramDeriverFactory)
        ).process(request, context);

        // 3.1.validate
        Messages errors = context.getMessages().getMessagesWithSeverity(Severity.ERROR);
        if (errors.size() > 0) {
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setMessages(context.getMessages().getMessageList());
            return serviceResponse;
        }

        // 4.call identified ServiceExecutor
        Object response = serviceExecutor.execute(serviceMetadata.getExecutionControl(), paramValueHolders, context);

        // 4.1.validate
        errors = context.getMessages().getMessagesWithSeverity(Severity.ERROR);
        if (errors.size() > 0) {
            ServiceResponse serviceResponse = new ServiceResponse();
            serviceResponse.setMessages(context.getMessages().getMessageList());
            return serviceResponse;
        }

        // 4.2.wrap and return response
        return wrap(response, context.getMessages());
    }

    private Object wrap(Object response, List<MessageData> messages) {
        try {
            String json = MAPPER.writeValueAsString(response);
            JsonNode jsonNode = MAPPER.readTree(json);
            ObjectNode objectNode = (ObjectNode) jsonNode;
            // write message to response
            if (!messages.isEmpty()) {
                ArrayNode messageArrayNode = objectNode.putArray("messages");
                for (MessageData message : messages) {
                    messageArrayNode.add(MAPPER.convertValue(message, JsonNode.class));
                }
            }
            return jsonNode;
        } catch (Exception e) {
            LOGGER.error("{} exception occurred when trying to wrap response", e);
            return response;
        }
    }
}
