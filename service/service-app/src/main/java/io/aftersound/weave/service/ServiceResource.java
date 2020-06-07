package io.aftersound.weave.service;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.service.cache.CacheRegistry;
import io.aftersound.weave.service.cache.KeyGenerator;
import io.aftersound.weave.service.request.HttpServletRequestWrapper;
import io.aftersound.weave.service.request.ParameterProcessor;
import io.aftersound.weave.service.runtime.ServiceDelegate;
import io.aftersound.weave.service.runtime.ServiceExecutorFactory;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Dynamically realized services entry point
 */
@Service
@Path("{var:.+}")
public class ServiceResource {

    private final ServiceMetadataRegistry adminServiceMetadataRegistry;
    private final ServiceExecutorFactory adminServiceExecutorFactory;
    private final ServiceMetadataRegistry serviceMetadataRegistry;
    private final ServiceExecutorFactory serviceExecutorFactory;
    private final ParameterProcessor<HttpServletRequest> parameterProcessor;
    private final CacheRegistry cacheRegistry;
    private final ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry;

    public ServiceResource(
            ServiceMetadataRegistry adminServiceMetadataRegistry,
            ServiceExecutorFactory adminServiceExecutorFactory,
            ServiceMetadataRegistry serviceMetadataRegistry,
            ServiceExecutorFactory serviceExecutorFactory,
            ParameterProcessor<HttpServletRequest> parameterProcessor,
            CacheRegistry cacheRegistry,
            ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry) {
        this.adminServiceMetadataRegistry = adminServiceMetadataRegistry;
        this.adminServiceExecutorFactory = adminServiceExecutorFactory;
        this.serviceMetadataRegistry = serviceMetadataRegistry;
        this.serviceExecutorFactory = serviceExecutorFactory;
        this.parameterProcessor = parameterProcessor;
        this.cacheRegistry = cacheRegistry;
        this.cacheKeyGeneratorRegistry = cacheKeyGeneratorRegistry;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request) {
        return serve(request, null);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(@Context HttpServletRequest request, String entity) {
        return serve(request, entity);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@Context HttpServletRequest request) {
        return serve(request, null);
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(@Context HttpServletRequest request, String entity) {
        return serve(request, entity);
    }

    @PATCH
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response patch(@Context HttpServletRequest request, String entity) {
        return serve(request, entity);
    }

    @HEAD
    @Produces(MediaType.APPLICATION_JSON)
    public Response head(@Context HttpServletRequest request) {
        return serve(request, null);
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    public Response options(@Context HttpServletRequest request) {
        return serve(request, null);
    }

    private Response serve(HttpServletRequest request, String entity) {
        String requestURI = request.getRequestURI();
        boolean isAdminServiceRequest = (requestURI.startsWith("/management/") || requestURI.startsWith("/discovery"));

        ServiceDelegate serviceDelegate;
        if (isAdminServiceRequest) {
            serviceDelegate = new ServiceDelegate(
                    adminServiceMetadataRegistry,
                    adminServiceExecutorFactory,
                    parameterProcessor,
                    cacheRegistry,
                    cacheKeyGeneratorRegistry
            );
        } else {
            serviceDelegate = new ServiceDelegate(
                    serviceMetadataRegistry,
                    serviceExecutorFactory,
                    parameterProcessor,
                    cacheRegistry,
                    cacheKeyGeneratorRegistry
            );
        }

        return serviceDelegate.serve(new HttpServletRequestWrapper(request, entity));
    }

}
