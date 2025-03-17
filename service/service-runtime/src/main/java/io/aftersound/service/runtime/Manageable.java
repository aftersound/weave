package io.aftersound.service.runtime;

public interface Manageable<ENTITY> {
    ManagementFacade<ENTITY> getManagementFacade();
}
