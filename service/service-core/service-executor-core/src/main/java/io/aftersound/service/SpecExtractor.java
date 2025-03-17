package io.aftersound.service;

public interface SpecExtractor<SPEC> {
    SPEC extract(Object source);
}
