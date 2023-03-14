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
    FindNamespaceHistory(Scope.Namespace, "find_history"),

    CreateApplication(Scope.Application, "create"),
    UpdateApplication(Scope.Application, "update"),
    DeleteApplication(Scope.Application, "delete"),
    GetApplication(Scope.Application, "get"),
    FindApplications(Scope.Application, "find"),
    FindApplicationHistory(Scope.Application, "find_history"),

    CreateRuntimeSpec(Scope.RuntimeSpec, "create"),
    UpdateRuntimeSpec(Scope.RuntimeSpec, "update"),
    DeleteRuntimeSpec(Scope.RuntimeSpec, "delete"),
    GetRuntimeSpec(Scope.RuntimeSpec, "get"),
    FindRuntimeSpecs(Scope.RuntimeSpec, "find"),
    FindRuntimeSpecHistory(Scope.RuntimeSpec, "find_history"),

    CreateOpenAPISpec(Scope.OpenAPISpec, "create"),
    UpdateOpenAPISpec(Scope.OpenAPISpec, "update"),
    DeleteOpenAPISpec(Scope.OpenAPISpec, "delete"),
    GetOpenAPISpec(Scope.OpenAPISpec, "get"),
    FindOpenAPISpecs(Scope.OpenAPISpec, "find"),
    FindOpenAPISpecHistory(Scope.OpenAPISpec, "find_history"),

    RegisterInstance(Scope.Instance, "register"),
    UnregisterInstance(Scope.Instance, "unregister"),
    ReceiveHeartbeat(Scope.Instance, "receive_heartbeat"),
    MarkDownInstance(Scope.Instance, "mark_down"),
    MarkUpInstance(Scope.Instance, "mark_up"),
    FindInstances(Scope.Instance, "find")
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
