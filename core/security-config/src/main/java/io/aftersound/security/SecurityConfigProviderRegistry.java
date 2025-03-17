package io.aftersound.security;

public final class SecurityConfigProviderRegistry {

    private static final SecurityConfigProviderRegistry INSTANCE = new SecurityConfigProviderRegistry();

    private SecurityConfigProvider securityConfigProvider;

    private SecurityConfigProviderRegistry() {
    }

    public static SecurityConfigProviderRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * Register {@link SecurityConfigProvider} once and only once
     * @param securityConfigProvider
     */
    public synchronized void register(SecurityConfigProvider securityConfigProvider) {
        if (securityConfigProvider == null) {
            throw new IllegalArgumentException("Null SecurityConfigProvider is not accepted");
        }
        if (this.securityConfigProvider == null) {
            this.securityConfigProvider = securityConfigProvider;
        }
    }

    public SecurityConfigProvider getProvider() {
        return securityConfigProvider;
    }

}
