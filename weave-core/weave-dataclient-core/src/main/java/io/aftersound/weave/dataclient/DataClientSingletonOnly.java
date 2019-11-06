package io.aftersound.weave.dataclient;

import java.util.Map;

/**
 * For certain type of database, it is not allowed for a client application
 * instance to have more than 1 instance of connection or client object. For
 * example, Couchbase doesn't allow several Cluster objects exists in the
 * same JVM runtime for the same Couchbase cluster.
 * To prevent a {@link DataClientFactory}, which creates/destroys data client
 * for such database, from creating more than 1 instance of data client, the
 * {@link DataClientFactory} much implement this interface.
 * {@link DataClientFactory#create} will leverage {@link Signature} to make
 * sure no more than 1 instance of data client would be created.
 */
public interface DataClientSingletonOnly<SIGNATURE extends Signature> {

    /**
     * get a signature derived from options
     * @param options
     *          - options used by {@link DataClientFactory} to get hold of data client instance
     * @return
     *          {@link Signature} of options
     */
    SIGNATURE getSignature(Map<String, Object> options);
}
