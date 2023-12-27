package io.aftersound.weave.service.rl;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.common.Severity;
import io.aftersound.weave.service.message.Message;
import io.aftersound.weave.utils.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.util.Collections;
import java.util.Map;

@Named("rate-limit-filter")
@Priority(Priorities.AUTHORIZATION + 100)
@Provider
public class WeaveRateLimitFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeaveRateLimitFilter.class);

    @Context
    protected HttpServletRequest request;

    private final RateLimitControlRegistry rateLimitControlRegistry;
    private final ActorRegistry<RateLimitEvaluator> rateLimitEvaluatorRegistry;

    public WeaveRateLimitFilter(
            RateLimitControlRegistry rateLimitControlRegistry,
            ActorRegistry<RateLimitEvaluator> rateLimitEvaluatorRegistry) {
        this.rateLimitControlRegistry = rateLimitControlRegistry;
        this.rateLimitEvaluatorRegistry = rateLimitEvaluatorRegistry;
    }

    @Override
    public void filter(ContainerRequestContext requestContext) {
        RateLimitControl rateLimitControl = rateLimitControlRegistry.getRateLimitControl(request.getMethod(), request.getRequestURI());

        // rate limit is not required
        if (rateLimitControl == null) {
            return;
        }

        RateLimitEvaluator rateLimitEvaluator = rateLimitEvaluatorRegistry.get(rateLimitControl.getType());
        if (rateLimitEvaluator != null) {
            RateLimitDecision decision = rateLimitEvaluator.evaluate(rateLimitControl, request);
            if (decision.isBlock()) {
                requestContext.abortWith(createRateLimitResponse());
            }
        } else {
            LOGGER.error("No RateLimitHandler for '{}' is available, request will be served", rateLimitControl.getType());
        }
    }

    private Response createRateLimitResponse() {
        final Message error = new Message();
        error.setSeverity(Severity.Error);
        error.setContent("Too many requests");

        Map<String, Object> errorResponseEntity = MapBuilder.hashMap()
                .kv("messages", Collections.singleton(error))
                .build();
        return Response
                .status(Response.Status.TOO_MANY_REQUESTS)
                .type(getExpectedResponseMediaType(request))
                .entity(errorResponseEntity)
                .build();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
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
}
