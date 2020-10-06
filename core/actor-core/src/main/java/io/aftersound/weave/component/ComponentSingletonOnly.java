package io.aftersound.weave.component;

import java.util.Map;

/**
 * For certain type of component, it is not allowed for enclosing application
 * instance to have more than 1 instance of same component type. For example,
 * Couchbase doesn't allow several Cluster objects exists in the same JVM
 * runtime for the same Couchbase cluster.
 * To prevent a {@link ComponentFactory} from creating more than 1 instance
 * of component, the {@link ComponentFactory} must implement this interface.
 * {@link ComponentFactory#create} will leverage {@link Signature} to make
 * sure no more than 1 instance of same component type would be created.
 */
public interface ComponentSingletonOnly<SIGNATURE extends Signature> {

    /**
     * get a signature derived from options
     * @param options
     *          - options used by {@link ComponentFactory} to get hold of data client instance
     * @return
     *          {@link Signature} of options
     */
    SIGNATURE getSignature(Map<String, String> options);
}
