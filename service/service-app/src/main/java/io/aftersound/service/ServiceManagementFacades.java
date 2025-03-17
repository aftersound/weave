package io.aftersound.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.jackson.ObjectMapperBuilder;
import io.aftersound.service.runtime.ManagementFacade;
import io.aftersound.service.runtime.ManagementFacades;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

@ManagedResource
public class ServiceManagementFacades implements ServiceManagementFacadesMBean {

    private static final ObjectMapper MAPPER = ObjectMapperBuilder.forJson().build();

    private final ManagementFacades managementFacades;

    ServiceManagementFacades(ManagementFacades managementFacades) {
        this.managementFacades = managementFacades;
    }

    @ManagedOperation
    @Override
    public boolean refreshManagedEntities(String facadeName) {
        ManagementFacade<?> managementFacade = managementFacades.get(facadeName);
        if (managementFacade != null) {
            managementFacade.refresh();
            return true;
        } else {
            return false;
        }
    }

    @ManagedOperation
    @Override
    public String listManagedEntities(String facadeName) {
        ManagementFacade<?> managementFacade = managementFacades.get(facadeName);
        if (managementFacade != null) {
            try {
                return MAPPER.writeValueAsString(managementFacade.list());
            } catch (Exception e) {
                return e.toString();
            }
        } else {
            return null;
        }
    }

    @ManagedOperation
    @Override
    public String getManagedEntity(String facadeName, String id) {
        ManagementFacade<?> managementFacade = managementFacades.get(facadeName);
        if (managementFacade != null) {
            Object obj = managementFacade.get(id);
            if (obj == null) {
                return null;
            }
            try {
                return MAPPER.writeValueAsString(obj);
            } catch (Exception e) {
                return e.toString();
            }
        } else {
            return null;
        }
    }

}
