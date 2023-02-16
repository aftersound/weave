package io.aftersound.weave.service.management;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

enum Operation {

    CreateNamespace(Scope.Namespace, "create"),
    UpdateNamespace(Scope.Namespace, "update"),
    DeleteNamespace(Scope.Namespace, "delete"),
    GetNamespace(Scope.Namespace, "get"),
    FindNamespaces(Scope.Namespace, "find"),

    CreateRuntimeConfig(Scope.RuntimeConfig, "create"),
    UpdateRuntimeConfig(Scope.RuntimeConfig, "update"),
    DeleteRuntimeConfig(Scope.RuntimeConfig, "delete"),
    GetRuntimeConfig(Scope.RuntimeConfig, "get"),
    FindRuntimeConfigs(Scope.RuntimeConfig, "find"),

    ;

    private final Scope scope;
    private final String shortName;

    Operation(Scope scope, String shortName) {
        this.scope = scope;
        this.shortName = shortName;
    }

    private static final Map<Scope, Map<String, Operation>> OPERATIONS_BY_SCOPE;

    static {
        Map<Scope, Map<String, Operation>> operationsByScope = new HashMap<>();
        for (Scope scope : Scope.values()) {
            operationsByScope.put(scope, new HashMap<>());
        }
        for (Operation operation : Operation.values()) {
            Map<String, Operation> operations = operationsByScope.get(operation.scope);
            operations.put(operation.name(), operation);
            operations.put(operation.shortName, operation);
        }

        for (Scope scope : Scope.values()) {
            Map<String, Operation> operations = Collections.unmodifiableMap(operationsByScope.get(scope));
            operationsByScope.put(scope, operations);
        }
        OPERATIONS_BY_SCOPE = Collections.unmodifiableMap(operationsByScope);
    }

    static Operation byScopeAndName(Scope scope, String name) {
        Map<String, Operation> operations = OPERATIONS_BY_SCOPE.get(scope);
        if (operations != null) {
            return operations.get(name);
        } else {
            return null;
        }
    }

}
