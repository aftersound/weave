package io.aftersound.weave.swift;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.model.Account;

import java.util.Map;

public class SwiftAccountFactory extends DataClientFactory<Account> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("SWIFT", Endpoint.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("SWIFT", Account.class);

    public SwiftAccountFactory(DataClientRegistry dataClientRegistry) {
        super(dataClientRegistry);
    }

    @Override
    protected Account createDataClient(Map<String, Object> options) {
        Settings settings = Settings.from(options);
        AccountConfig config = new AccountConfig();
        config.setUsername(settings.username());
        config.setPassword(settings.password());
        config.setAuthUrl(settings.authUrl());
        config.setTenantId(settings.tenantId());
        config.setTenantName(settings.tenantName());
        config.setPreferredRegion(settings.preferredRegion());
        config.setDisableSslValidation(settings.sslValidationDisabled());
        config.setSocketTimeout(settings.socketTimeout());
        config.setMock(settings.mockEnabled());
        return new AccountFactory(config).createAccount();
    }

    @Override
    protected void destroyDataClient(Account account) {
        // nothing needs to be done
    }
}
