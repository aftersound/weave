package io.aftersound.weave.service.runtime;

import java.util.*;

class ManagementFacadesImpl implements ManagementFacades {

    private final Map<String, ManagementFacade<?>> facadeByName;

    ManagementFacadesImpl(Manageable<?>... manageables) {
        Map<String, ManagementFacade<?>> byScope = new LinkedHashMap<>();
        for (Manageable<?> manageable : manageables) {
            ManagementFacade<?> facade = manageable.getManagementFacade();
            byScope.put(facade.name(), facade);
        }
        this.facadeByName = Collections.unmodifiableMap(byScope);
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
