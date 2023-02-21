package io.aftersound.weave.service.management;

public enum Scope {
    // Namespace management
    Namespace,

    // Application Management
    // 1 namespace can have 0..N applications
    Application,

    // Runtime Config Management
    // 1 application has 1 runtime config
    RuntimeConfig
}
