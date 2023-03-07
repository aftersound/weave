package io.aftersound.weave.service;

public class ServiceInstanceHolder {

    private static ServiceInstance serviceInstance;

    public static void set(ServiceInstance serviceInstance) {
        ServiceInstanceHolder.serviceInstance = serviceInstance;
    }

    public static ServiceInstance get() {
        return serviceInstance;
    }

}
