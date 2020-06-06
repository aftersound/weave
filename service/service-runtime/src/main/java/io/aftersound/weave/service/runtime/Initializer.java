package io.aftersound.weave.service.runtime;

public interface Initializer {
    void init(boolean tolerateException) throws Exception;
}
