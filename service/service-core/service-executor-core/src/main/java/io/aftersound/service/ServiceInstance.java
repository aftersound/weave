package io.aftersound.service;

public interface ServiceInstance {

    /**
     * @return the identifier of this instance, expected to be unique across board
     */
    String getId();

    /**
     * @return namespace this instance belongs to
     */
    String getNamespace();

    /**
     * @return the application this instance belongs to
     */
    String getApplication();

    /**
     * @return the environment this instance runs in
     */
    String getEnvironment();

    /**
     * @return the host of this instance
     */
    String getHost();

    /**
     * @return the (main) port of this instance
     */
    int getPort();

    /**
     * @return the IPV4 address of this instance
     */
    String getIpv4Address();

    /**
     * @return the IPV6 address of this instance
     */
    String getIpv6Address();
}
