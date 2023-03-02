package io.aftersound.weave.service;

public interface ServiceInstance {
    String getNamespace();
    String getApplication();
    String getEnvironment();
    String getHost();
    int getPort();
    String getIpv4Address();
    String getIpv6Address();
}
