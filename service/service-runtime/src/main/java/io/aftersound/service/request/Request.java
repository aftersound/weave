package io.aftersound.service.request;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;

import java.io.InputStream;

public class Request {

    private final ContainerRequestContext requestContext;

    public Request(ContainerRequestContext requestContext) {
        this.requestContext = requestContext;
    }

    public String getMethod() {
        return requestContext.getMethod();
    }

    public String getPath() {
        return requestContext.getUriInfo().getAbsolutePath().getRawPath();
    }

    public MediaType getAccept() {
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

    public String getHeader(String name) {
        return requestContext.getHeaderString(name);
    }

    public MultivaluedMap<String, String> getQueryParameters() {
        return requestContext.getUriInfo().getQueryParameters();
    }

    public InputStream getEntityStream() {
        return requestContext.getEntityStream();
    }

}
