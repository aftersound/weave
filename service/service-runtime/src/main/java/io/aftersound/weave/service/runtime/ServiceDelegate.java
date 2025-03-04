package io.aftersound.weave.service.runtime;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import io.aftersound.func.Directive;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.msg.Message;
import io.aftersound.msg.Messages;
import io.aftersound.msg.Severity;
import io.aftersound.schema.Constraint;
import io.aftersound.schema.ProtoTypes;
import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.ServiceExecutor;
import io.aftersound.weave.service.ServiceMetadataRegistry;
import io.aftersound.weave.service.cache.CacheControl;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyControl;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.message.MessageRegistry;
import io.aftersound.weave.service.metadata.ExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.ParamType;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.request.RequestProcessor;
import io.aftersound.weave.service.response.ServiceResponse;
import io.opentracing.Scope;
import io.opentracing.Span;
import io.opentracing.util.GlobalTracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * This {@link ServiceDelegate} is called by service controller to
 * serve request(s).
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ServiceDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceDelegate.class);
    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private static final ParamField PARAM_FIELD_DIAG;
    static {
        ParamField f = new ParamField();
        f.setParamType(ParamType.Query);
        f.setName("_diag");
        f.setType(ProtoTypes.INTEGER.create());
        f.setConstraint(Constraint.optional());
        f.setDirectives(Collections.singletonList(
                Directive.of("t", "TRANSFORM", "INT:FROM(String)")
        ));
        f.initDirectives(MasterFuncFactory.instance());
        PARAM_FIELD_DIAG = f;
    }
    private static final Integer DIAG_ECHO_PARSED_PARAM_VALUES = 1;

    private final ServiceMetadataRegistry serviceMetadataRegistry;
    private final ServiceExecutorFactory serviceExecutorFactory;
    private final ParameterProcessor<HttpServletRequest> parameterProcessor;
    private final CacheRegistry cacheRegistry;
    private final ActorRegistry<KeyGenerator> keyGeneratorRegistry;

    public ServiceDelegate(
            ServiceMetadataRegistry serviceMetadataRegistry,
            ServiceExecutorFactory serviceExecutorFactory,
            ParameterProcessor<HttpServletRequest> parameterProcessor,
            CacheRegistry cacheRegistry,
            ActorRegistry<KeyGenerator> keyGeneratorRegistry) {
        this.serviceMetadataRegistry = serviceMetadataRegistry;
        this.serviceExecutorFactory = serviceExecutorFactory;
        this.parameterProcessor = parameterProcessor;
        this.cacheRegistry = cacheRegistry;
        this.keyGeneratorRegistry = keyGeneratorRegistry;
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
     *          a {@link Response}, which wraps entity response from
     *          {@link ServiceExecutor#execute(ExecutionControl, ParamValueHolders, ServiceContext)}
     */
    public Response serve(HttpServletRequest request) {

        MediaType mediaType = getExpectedResponseMediaType(request);

        Span span = GlobalTracer.get().buildSpan(request.getRequestURI())
                .withTag("method", "ServiceDelegate:serve")
                .start();
        try (Scope scope = GlobalTracer.get().scopeManager().activate(span)) {
            ServiceContext context = new ServiceContext();

            // 1.identify ServiceMetadata
            ServiceMetadata serviceMetadata = serviceMetadataRegistry.matchServiceMetadata(
                    request.getMethod(),
                    request.getRequestURI(),
                    new HashMap<>()
            );

            // 1.1.validate
            if (serviceMetadata == null) {
                ServiceResponse serviceResponse = new ServiceResponse();
                serviceResponse.setMessages(
                        Collections.singletonList(
                                MessageRegistry.NO_RESOURCE.error(request.getRequestURI())
                        )
                );
                return Response.status(Response.Status.NOT_FOUND)
                        .type(mediaType)
                        .entity(serviceResponse)
                        .build();
            }

            // 2.identify ServiceExecutor
            ServiceExecutor serviceExecutor = serviceExecutorFactory.getServiceExecutor(serviceMetadata);

            // 2.1.validate
            if (serviceExecutor == null) {
                ServiceResponse serviceResponse = new ServiceResponse();
                serviceResponse.setMessages(
                        Collections.singletonList(
                                MessageRegistry.NO_SERVICE_EXECUTOR.error(request.getRequestURI())
                        )
                );
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .type(mediaType)
                        .entity(serviceResponse)
                        .build();
            }

            // 3.Process and extract request parameters
            ParamValueHolders paramValueHolders;
            try {
                paramValueHolders = new RequestProcessor<>(parameterProcessor)
                        .process(request, getExtendedParamFields(serviceMetadata), context);
            } catch (Exception e) {
                LOGGER.error(
                        "Exception occurred on parsing request based on service metadata for {}",
                        serviceMetadata.getPath(),
                        e
                );

                context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error("Failed to parse request"));

                ServiceResponse serviceResponse = new ServiceResponse();
                serviceResponse.setMessages(context.getMessages().list());

                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .type(mediaType)
                        .entity(serviceResponse)
                        .build();
            }

            // fast return if _diag=1 exist
            if (paramValueHolders.firstWithName(PARAM_FIELD_DIAG.getName()).is(DIAG_ECHO_PARSED_PARAM_VALUES)) {
                return Response.status(Response.Status.OK)
                        .type(mediaType)
                        .entity(paramValueHolders.asUnmodifiableMap())
                        .build();
            }

            // 3.1.validate
            Messages errors = context.getMessages().getMessagesWithSeverity(Severity.ERROR);
            if (errors.size() > 0) {
                ServiceResponse serviceResponse = new ServiceResponse();
                serviceResponse.setMessages(context.getMessages().list());
                return Response.status(Response.Status.BAD_REQUEST)
                        .type(mediaType)
                        .entity(serviceResponse)
                        .build();
            }

            // 4.try cached response
            ResponseCacheHandle responseCacheHandle = new ResponseCacheHandle(
                    serviceMetadata.getPath(),
                    serviceMetadata.getCacheControl(),
                    paramValueHolders
            );
            Object cachedResponse = responseCacheHandle.tryGetCachedResponse();
            if (cachedResponse != null) {
                return Response.status(Response.Status.OK)
                        .type(mediaType)
                        .entity(cachedResponse)
                        .build();
            }

            // 5.call identified ServiceExecutor
            Object response;
            try {
                response = serviceExecutor.execute(serviceMetadata.getExecutionControl(), paramValueHolders, context);
            } catch (Exception e) {
                LOGGER.error("{} failed to serve request:\n", serviceExecutor.getClass().getName(), e);

                context.getMessages().addMessage(MessageRegistry.INTERNAL_SERVICE_ERROR.error("failed to serve request"));

                ServiceResponse serviceResponse = new ServiceResponse();
                serviceResponse.setMessages(context.getMessages().list());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .type(mediaType)
                        .entity(serviceResponse)
                        .build();
            }

            // 5.1.validate
            if (context.responseStatus() >= 400) {
                ServiceResponse serviceResponse = new ServiceResponse();
                serviceResponse.setMessages(context.getMessages().list());
                return Response.status(context.responseStatus())
                        .type(mediaType)
                        .entity(serviceResponse)
                        .build();
            }

            // 5.2.wrap response
            Object wrappedResponse = wrap(response, context.getMessages().list());

            // 6.try to cache response
            responseCacheHandle.tryCacheResponse(wrappedResponse);

            return Response.status(Response.Status.OK)
                    .type(mediaType)
                    .entity(wrappedResponse)
                    .build();

        }
    }

    private MediaType getExpectedResponseMediaType(HttpServletRequest request) {
        String accept = request.getHeader("Accept");
        if (accept != null) {
            try {
                return MediaType.valueOf(accept);
            } catch (Exception e) {
                return MediaType.APPLICATION_JSON_TYPE;
            }
        } else {
            return MediaType.APPLICATION_JSON_TYPE;
        }
    }

    private List<ParamField> getExtendedParamFields(ServiceMetadata serviceMetadata) {
        List<ParamField> paramFields = new ArrayList<>(serviceMetadata.getParamFields().size() + 1);
        paramFields.addAll(serviceMetadata.getParamFields());
        paramFields.add(PARAM_FIELD_DIAG);
        return paramFields;
    }

    private Object wrap(Object response, List<Message> messages) {
        if (messages.isEmpty()) {
            return response;
        }
        try {
            String json = MAPPER.writeValueAsString(response);
            JsonNode jsonNode = MAPPER.readTree(json);
            ObjectNode objectNode = (ObjectNode) jsonNode;

            // write message to response
            ArrayNode messageArrayNode = objectNode.putArray("messages");
            for (Message message : messages) {
                messageArrayNode.add(MAPPER.convertValue(message, JsonNode.class));
            }
            return jsonNode;
        } catch (Exception e) {
            LOGGER.error("Exception occurred when trying to wrap response", e);
            return response;
        }
    }

    private class ResponseCacheHandle {

        private final Cache responseCache;
        private final Object responseCacheKey;

        ResponseCacheHandle(String id, CacheControl cacheControl, ParamValueHolders paramValueHolders) {
            this.responseCache = tryGetResponseCache(id, cacheControl);
            this.responseCacheKey = tryGetResponseCacheKey(cacheControl, paramValueHolders);
        }

        private Cache tryGetResponseCache(String id, CacheControl cacheControl) {
            if (cacheControl != null) {
                return cacheRegistry.getCache(id);
            } else {
                return null;
            }
        }

        private Object tryGetResponseCacheKey(CacheControl cacheControl, ParamValueHolders paramValueHolders) {
            if (cacheControl == null || cacheControl.getKeyControl() == null) {
                return null;
            }

            KeyControl keyControl = cacheControl.getKeyControl();
            if (keyControl == null) {
                return null;
            }
            KeyGenerator keyGenerator = keyGeneratorRegistry.get(keyControl.getType());

            try {
                return keyGenerator.generateKey(keyControl, paramValueHolders);
            } catch (Exception e) {
                LOGGER.error("Failed to generate cache key", e);
                return null;
            }
        }

        Object tryGetCachedResponse() {
            if (responseCache != null && responseCacheKey != null) {
                return responseCache.getIfPresent(responseCacheKey);
            } else {
                return null;
            }
        }

        boolean tryCacheResponse(Object response) {
            if (responseCache != null && responseCacheKey != null && response != null) {
                responseCache.put(responseCacheKey, response);
                return true;
            } else {
                return false;
            }
        }

    }

}
