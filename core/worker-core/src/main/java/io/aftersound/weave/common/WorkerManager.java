package io.aftersound.weave.common;

import java.util.Map;

public interface WorkerManager {

    /**
     * Get the configuration for the worker with specified id and in specified group
     *
     * @param id    the identifier of the worker
     * @param group the group of the worker
     * @return the configuration for the worker identified by id and group
     */
    Map<String, Object> getConfig(String id, String group);
}
