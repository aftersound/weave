package io.aftersound.weave.couchbase;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;

import java.util.Map;

public class CouchbaseClusterFactory extends DataClientFactory<Cluster> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("CouchbaseCluster", Endpoint.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("CouchbaseCluster", Cluster.class);

    public CouchbaseClusterFactory(DataClientRegistry dataClientRegistry) {
        super(dataClientRegistry);
    }

    @Override
    protected Cluster createDataClient(Map<String, Object> options) {
        Settings settings = Settings.from(options);
        Cluster cluster = CouchbaseCluster.create(settings.getNodes());
        cluster.authenticate(settings.getUsername(), settings.getPassword());
        return cluster;
    }

    @Override
    protected void destroyDataClient(Cluster cluster) {
        // Note: disconnect Couchbase cluster will close all open buckets
        if (cluster != null) {
            cluster.disconnect();
        }
    }
}
