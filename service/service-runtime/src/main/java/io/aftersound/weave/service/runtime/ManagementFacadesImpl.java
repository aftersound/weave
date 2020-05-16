package io.aftersound.weave.service.runtime;

import java.util.*;

class ManagementFacadesImpl implements ManagementFacades {

    private final Map<String, ManagementFacade<?>> facadeByScope;

    ManagementFacadesImpl(Manageable<?>... manageables) {
        Map<String, ManagementFacade<?>> byScope = new LinkedHashMap<>();
        for (Manageable<?> manageable : manageables) {
            ManagementFacade<?> facade = manageable.getManagementFacade();
            byScope.put(facade.scope(), facade);
        }
        this.facadeByScope = Collections.unmodifiableMap(byScope);
    }

    @Override
    public List<String> getScopes() {
        return new ArrayList<>(facadeByScope.keySet());
    }

    @Override
    public ManagementFacade<?> get(String scope) {
        return facadeByScope.get(scope);
    }

}
