package io.aftersound.weave.service.runtime;

import java.util.List;

public interface ManagementFacades {
    List<String> getScopes();
    ManagementFacade<?> get(String scope);
}
