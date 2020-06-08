package io.aftersound.weave.service.runtime;

import java.util.*;

class ManagementFacadesImpl implements ManagementFacades {

    private final Map<String, ManagementFacade<?>> facadeByName;

    ManagementFacadesImpl(Manageable<?>... manageables) {
        Map<String, ManagementFacade<?>> byFacadeName = new LinkedHashMap<>();
        for (Manageable<?> manageable : manageables) {
            ManagementFacade<?> facade = manageable.getManagementFacade();
            byFacadeName.put(facade.name(), facade);
        }
        this.facadeByName = Collections.unmodifiableMap(byFacadeName);
    }

    @Override
    public List<String> getFacades() {
        return new ArrayList<>(facadeByName.keySet());
    }

    @Override
    public ManagementFacade<?> get(String scope) {
        return facadeByName.get(scope);
    }

}
