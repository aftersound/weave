package io.aftersound.service.runtime;

import java.util.List;

public interface ManagementFacades {
    List<String> getFacades();
    ManagementFacade<?> get(String facadeName);
}
