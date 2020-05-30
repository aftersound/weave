package io.aftersound.weave.service.metadata;

/**
 * Implement this interface if something needs to be and could be instantiated
 * with information in {@link ExecutionControl}
 */
public interface Initializable {
    void initialize();
    <T> T initialized(Class<T> type);
}
