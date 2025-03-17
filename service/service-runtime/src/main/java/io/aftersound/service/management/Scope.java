package io.aftersound.service.management;

public enum Scope {
    // Namespace management
    Namespace,

    // Application Management
    // 1 namespace can have 0..N applications
    Application,

    // Runtime Spec Management
    // 1 application has 1 runtime spec
    RuntimeSpec,

    // OpenAPI Spec Management
    // 1 application has 1 OpenAPI spec
    OpenAPISpec,

    // Service Instance Management
    // 1 application has 0..N instances
    Instance
}
