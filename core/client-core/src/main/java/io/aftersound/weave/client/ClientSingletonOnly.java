package io.aftersound.weave.client;

import java.util.Map;

/**
 * For certain type of database, it is not allowed for a client application
 * instance to have more than 1 instance of connection or client object. For
 * example, Couchbase doesn't allow several Cluster objects exists in the
 * same JVM runtime for the same Couchbase cluster.
 * To prevent a {@link ClientFactory}, which creates/destroys data client
 * for such database, from creating more than 1 instance of data client, the
 * {@link ClientFactory} much implement this interface.
 * {@link ClientFactory#create} will leverage {@link Signature} to make
 * sure no more than 1 instance of data client would be created.
 */
public interface ClientSingletonOnly<SIGNATURE extends Signature> {

    /**
     * get a signature derived from options
     * @param options
     *          - options used by {@link ClientFactory} to get hold of data client instance
     * @return
     *          {@link Signature} of options
     */
    SIGNATURE getSignature(Map<String, String> options);
}
