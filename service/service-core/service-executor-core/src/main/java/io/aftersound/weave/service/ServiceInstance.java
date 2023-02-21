package io.aftersound.weave.service;

public interface ServiceInstance {
    String getNamespace();
    String getApplication();
    String getEnvironment();
    String getHostName();
    String getHostAddress();
}
