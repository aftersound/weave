package io.aftersound.service;

import io.aftersound.actor.ActorRegistry;
import io.aftersound.service.cache.CacheRegistry;
import io.aftersound.service.cache.KeyGenerator;
import io.aftersound.service.request.ParameterProcessor;
import io.aftersound.service.request.Request;
import io.aftersound.service.runtime.ServiceDelegate;
import io.aftersound.service.runtime.ServiceExecutorFactory;
import jakarta.ws.rs.*;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Dynamically realized services entry point
 */
@Path("{var:.+}")
public class ServiceResource {

    private final ServiceMetadataRegistry serviceMetadataRegistry;
    private final ServiceExecutorFactory serviceExecutorFactory;
    private final ParameterProcessor<Request> parameterProcessor;
    private final CacheRegistry cacheRegistry;
    private final ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry;

    @Autowired
    public ServiceResource(
            ServiceMetadataRegistry serviceMetadataRegistry,
            ServiceExecutorFactory serviceExecutorFactory,
            ParameterProcessor<Request> parameterProcessor,
            CacheRegistry cacheRegistry,
            ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry) {
        this.serviceMetadataRegistry = serviceMetadataRegistry;
        this.serviceExecutorFactory = serviceExecutorFactory;
        this.parameterProcessor = parameterProcessor;
        this.cacheRegistry = cacheRegistry;
        this.cacheKeyGeneratorRegistry = cacheKeyGeneratorRegistry;
    }

    @GET
    @Produces(MediaType.WILDCARD)
    public Response get(@Context ContainerRequestContext request) {
        return serve(request);
    }

    @POST
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response post(@Context ContainerRequestContext request) {
        return serve(request);
    }

    @DELETE
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response delete(@Context ContainerRequestContext request) {
        return serve(request);
    }

    @PUT
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response put(@Context ContainerRequestContext request) {
        return serve(request);
    }

    @PATCH
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response patch(@Context ContainerRequestContext request) {
        return serve(request);
    }

    @HEAD
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response head(@Context ContainerRequestContext request) {
        return serve(request);
    }

    @OPTIONS
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.WILDCARD)
    public Response options(@Context ContainerRequestContext request) {
        return serve(request);
    }

    private Response serve(ContainerRequestContext requestContext) {
        ServiceDelegate serviceDelegate = new ServiceDelegate(
                serviceMetadataRegistry,
                serviceExecutorFactory,
                parameterProcessor,
                cacheRegistry,
                cacheKeyGeneratorRegistry
        );
        return serviceDelegate.serve(new Request(requestContext));
    }

}
