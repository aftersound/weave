package io.aftersound.weave.service;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.cache.KeyGenerator;
import io.aftersound.weave.service.request.Deriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Dynamically realized services entry point
 */
@Service
@Path("{var:.+}")
public class WeaveServiceResource {

    @Autowired
    @Qualifier("adminServiceMetadataManager")
    ServiceMetadataManager adminServiceMetadataManager;

    @Autowired
    @Qualifier("adminServiceExecutorFactory")
    ServiceExecutorFactory adminServiceExecutorFactory;

    @Autowired
    @Qualifier("serviceMetadataManager")
    ServiceMetadataManager serviceMetadataManager;

    @Autowired
    @Qualifier("serviceExecutorFactory")
    ServiceExecutorFactory serviceExecutorFactory;

    @Autowired
    ActorRegistry<Deriver> paramDeriverRegistry;

    @Autowired
    CacheRegistry cacheRegistry;

    @Autowired
    ActorRegistry<KeyGenerator> cacheKeyGeneratorRegistry;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response get(@Context HttpServletRequest request) {
        return serve(request);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response post(@Context HttpServletRequest request) {
        return serve(request);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@Context HttpServletRequest request) {
        return serve(request);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    public Response put(@Context HttpServletRequest request) {
        return serve(request);
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    public Response patch(@Context HttpServletRequest request) {
        return serve(request);
    }

    @HEAD
    @Produces(MediaType.APPLICATION_JSON)
    public Response head(@Context HttpServletRequest request) {
        return serve(request);
    }

    @OPTIONS
    @Produces(MediaType.APPLICATION_JSON)
    public Response options(@Context HttpServletRequest request) {
        return serve(request);
    }

    private Response serve(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        boolean isAdminServiceRequest = (requestURI.startsWith("/admin/") || requestURI.startsWith("/openapi"));
        if (isAdminServiceRequest) {
            return new ServiceDelegate(
                    adminServiceMetadataManager,
                    adminServiceExecutorFactory,
                    paramDeriverRegistry,
                    cacheRegistry,
                    cacheKeyGeneratorRegistry
            ).serve(request);
        } else {
            return new ServiceDelegate(
                    serviceMetadataManager,
                    serviceExecutorFactory,
                    paramDeriverRegistry,
                    cacheRegistry,
                    cacheKeyGeneratorRegistry
            ).serve(request);
        }
    }

}
