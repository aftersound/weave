package io.aftersound.weave.service.runtime;

import java.util.List;

public interface ManagementFacade<ENTITY> {
    String scope();
    Class<ENTITY> entityType();
    void refresh();
    List<ENTITY> list();
    ENTITY get(String id);
}
