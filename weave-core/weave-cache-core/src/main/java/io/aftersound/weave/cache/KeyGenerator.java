package io.aftersound.weave.cache;

public interface KeyGenerator<CONTROL extends KeyControl, SOURCE> {
    Object generateKey(CONTROL control, SOURCE source);
}
