package io.aftersound.weave.swift;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.utils.OptionsBuilder;
import org.javaswift.joss.model.Account;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

public class SwiftAccountFactoryTest {

    @Test
    public void testCreateClient() {
        ActorBindings<Endpoint, DataClientFactory<?>, Object> dcfBindings = new ActorBindings<>();
        DataClientRegistry clientRegistry = new DataClientRegistry(dcfBindings);
        SwiftAccountFactory factory = new SwiftAccountFactory(clientRegistry);

        Map<String, Object> options = new OptionsBuilder()
                .option("tenantId", "test")
                .option("tenantName", "test")
                .option("authUrl", "http://localhost:5000/v2.0/tokens")
                .option("username", "test")
                .option("password", "password")
                .option("preferredRegion", "Region1")
                .option("sslValidationDisabled", "true")
                .option("mockEnabled", "true")
                .build();
        Account account = factory.createDataClient(options);
        assertNotNull(account);
    }

}