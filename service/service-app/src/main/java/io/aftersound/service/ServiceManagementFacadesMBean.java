package io.aftersound.service;

public interface ServiceManagementFacadesMBean {
    boolean refreshManagedEntities(String facadeName);
    String listManagedEntities(String facadeName);
    String getManagedEntity(String facadeName, String id);
}
