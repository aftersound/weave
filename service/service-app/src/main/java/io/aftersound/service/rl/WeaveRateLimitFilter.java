package io.aftersound.service.rl;

import io.aftersound.actor.ActorRegistry;
import io.aftersound.msg.Message;
import io.aftersound.msg.Severity;
import io.aftersound.util.MapBuilder;
import jakarta.inject.Named;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import java.util.Collections;
import java.util.Map;

@SuppressWarnings({"rawtypes", "unchecked"})
@Named("rate-limit-filter")
@Priority(Priorities.AUTHORIZATION + 100)
@Provider
public class WeaveRateLimitFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeaveRateLimitFilter.class);

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
        RateLimitControl rateLimitControl = rateLimitControlRegistry.getRateLimitControl(
                requestContext.getMethod(), requestContext.getUriInfo().getPath());

        // rate limit is not required
        if (rateLimitControl == null) {
            return;
        }

        RateLimitEvaluator rateLimitEvaluator = rateLimitEvaluatorRegistry.get(rateLimitControl.getType());
        if (rateLimitEvaluator != null) {
            RateLimitDecision decision = rateLimitEvaluator.evaluate(rateLimitControl, requestContext);
            if (decision.isBlock()) {
                requestContext.abortWith(createRateLimitResponse(requestContext));
            }
        } else {
            LOGGER.error("No RateLimitHandler for '{}' is available, request will be served", rateLimitControl.getType());
        }
    }

    private Response createRateLimitResponse(ContainerRequestContext requestContext) {
        final Message error = new Message();
        error.setSeverity(Severity.ERROR);
        error.setContent("Too many requests");

        Map<String, Object> errorResponseEntity = MapBuilder.<String, Object>hashMap()
                .put("messages", Collections.singleton(error))
                .build();
        return Response
                .status(Response.Status.TOO_MANY_REQUESTS)
                .type(getExpectedResponseMediaType(requestContext))
                .entity(errorResponseEntity)
                .build();
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) {
    }

    private MediaType getExpectedResponseMediaType(ContainerRequestContext requestContext) {
        String accept = requestContext.getHeaderString("Accept");
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
