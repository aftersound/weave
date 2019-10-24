package io.aftersound.weave.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.document.JsonDocument;
import com.couchbase.client.java.document.json.JsonArray;
import com.couchbase.client.java.document.json.JsonObject;
import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import io.aftersound.weave.utils.OptionsBuilder;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class CouchbaseBucketFactoryTest {

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
        Bucket bucket = cluster.openBucket("testBucket1");
        assertNotNull(bucket);

        JsonObject arthur = JsonObject.create()
                .put("name", "Arthur")
                .put("email", "kingarthur@couchbase.com")
                .put("interests", JsonArray.from("Holy Grail", "African Swallows"));

        // Store the Document
        bucket.upsert(JsonDocument.create("u:king_arthur", arthur));

        // Load the Document and print it
        // Prints Content and Metadata of the stored Document
        System.out.println(bucket.get("u:king_arthur"));
    }

    @Test
    public void destroyDataClient() {
    }
}