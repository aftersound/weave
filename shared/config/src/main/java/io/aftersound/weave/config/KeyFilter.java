package io.aftersound.weave.config;

public interface KeyFilter {
    boolean isAcceptable(Key<?> key);
}
