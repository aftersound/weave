package io.aftersound.weave.security;

public interface Authenticator {
    Authentication attemptAuthentication(String bearer);
}
