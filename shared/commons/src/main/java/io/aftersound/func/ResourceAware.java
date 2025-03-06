package io.aftersound.func;

import java.util.Map;
import java.util.Set;

public interface ResourceAware {
    Set<String> getResourceNames();
    void bindResources(Map<String, Object> resources);
}
