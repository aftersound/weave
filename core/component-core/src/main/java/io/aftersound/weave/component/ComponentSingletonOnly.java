package io.aftersound.weave.component;

/**
 * Tag interface for certain implementations of {@link ComponentFactory}.
 *
 * A {@link ComponentFactory} which has this tag interface is expected to
 * create one and only one component for {@link ComponentConfig}s which
 * share matching {@link Signature}.  {@link ComponentFactory#create} will
 * leverage {@link Signature} to make sure no more than 1 instance of same
 * component type would be created.
 *
 * For certain type of component, it is not allowed to have more than 1
 * instance of same component type for same or similar configuration. For
 * example, Couchbase doesn't allow several Cluster objects exists in the
 * same JVM runtime for the same Couchbase cluster.
 */
public interface ComponentSingletonOnly {
}
