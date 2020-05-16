package io.aftersound.weave.security;

public interface SecurityConfigProvider {
    SecurityConfig get(String id);
}
