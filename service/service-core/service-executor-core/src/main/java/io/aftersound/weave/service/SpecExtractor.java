package io.aftersound.weave.service;

public interface SpecExtractor<SPEC> {
    SPEC extract(Object source);
}
