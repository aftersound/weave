package io.aftersound.weave.service.message;

import javax.ws.rs.core.Response;

public interface StatusMapper {
    Response.Status getStatus(Messages errors);
}
