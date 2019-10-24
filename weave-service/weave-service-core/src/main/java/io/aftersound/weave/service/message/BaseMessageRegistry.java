package io.aftersound.weave.service.message;

import javax.ws.rs.core.Response.Status;

public abstract class BaseMessageRegistry {

    private static StatusMapper statusMapper = new StatusMapper() {

        @Override
        public Status getStatus(Messages messages) {
            return Status.OK;
        }

    };

    protected synchronized static final void bindStatusMapper(StatusMapper statusMapper) {
        if (statusMapper != null) {
            BaseMessageRegistry.statusMapper = statusMapper;
        }
    }

    public static final Status getMappedStatus(Messages messages) {
        return statusMapper.getStatus(messages);
    }

}
