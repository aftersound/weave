package io.aftersound.weave.service;

import io.aftersound.weave.actor.ActorFactory;
import io.aftersound.weave.cache.CacheRegistry;
import io.aftersound.weave.service.metadata.param.DeriveControl;
import io.aftersound.weave.service.request.Deriver;
import io.aftersound.weave.service.request.ParamValueHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
    ActorFactory<DeriveControl, Deriver, ParamValueHolder> paramDeriverFactory;

    @Autowired
    CacheRegistry cacheRegistry;

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
                    paramDeriverFactory,
                    cacheRegistry
            ).serve(request);
        } else {
            return new ServiceDelegate(
                    serviceMetadataManager,
                    serviceExecutorFactory,
                    paramDeriverFactory,
                    cacheRegistry
            ).serve(request);
        }
    }

}
