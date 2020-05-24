package io.aftersound.weave.service;

public interface ServiceInstance {
    String getApplicationName();
    String getNamespace();
    String getEnvironment();
    String getHostName();
    String getHostAddress();
}
