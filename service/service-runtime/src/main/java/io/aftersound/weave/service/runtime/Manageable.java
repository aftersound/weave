package io.aftersound.weave.service.runtime;

public interface Manageable<ENTITY> {
    ManagementFacade<ENTITY> getManagementFacade();
}
