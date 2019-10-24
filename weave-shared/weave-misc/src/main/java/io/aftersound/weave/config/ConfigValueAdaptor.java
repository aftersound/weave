package io.aftersound.weave.config;

public interface ConfigValueAdaptor<T> {
    T adapt(String rawValue);
}
