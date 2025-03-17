package io.aftersound.service.runtime;

public interface Initializer {
    void init(boolean tolerateException) throws Exception;
}
