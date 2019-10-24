package io.aftersound.weave.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;

import java.util.Map;

// TODO: how to deal with cluster?
// when cluster is closed, all open buckets will be closed
// seems better option is to allow DataClientFactory, hence CouchbaseBucketFactory stateful
// to keep track of connected clusters and open buckets of each connected cluster
public class CouchbaseBucketFactory extends DataClientFactory<Bucket> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("CouchbaseBucket", Endpoint.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("CouchbaseBucket", Bucket.class);

    public CouchbaseBucketFactory(DataClientRegistry dataClientRegistry) {
        super(dataClientRegistry);
    }

    @Override
    protected Bucket createDataClient(Map<String, Object> options) {
        Settings settings = Settings.from(options);
        Cluster cluster = dataClientRegistry.getClient(CBSignature.of(options));
        if (cluster == null) {
            cluster = CouchbaseCluster.create(settings.getNodes());
            cluster.authenticate(settings.getUsername(), settings.getPassword());
        }
        return cluster.openBucket(settings.getBucket());
    }

    @Override
    protected void destroyDataClient(Bucket bucket) {
        if (bucket != null) {
            bucket.close();
        }
    }
}
