package io.aftersound.weave.couchbase;

import com.couchbase.client.java.Cluster;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.utils.OptionsBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class CouchbaseClusterFactoryTest {

    @Test
    public void createDataClient() {
        Map<String, Object> options = new OptionsBuilder()
                .option("cluster", "test")
                .option("nodes", "localhost")
                .option("username", "testUser1")
                .option("password", "testUser1")
                .build();

        ActorBindings<Endpoint, DataClientFactory<?>, Object> dcfBindings = new ActorBindings<>();

        DataClientRegistry dcRegistry = new DataClientRegistry(dcfBindings);
        CouchbaseClusterFactory couchbaseClusterFactory = new CouchbaseClusterFactory(dcRegistry);
        Cluster cluster = couchbaseClusterFactory.createDataClient(options);
        assertNotNull(cluster);
    }

}