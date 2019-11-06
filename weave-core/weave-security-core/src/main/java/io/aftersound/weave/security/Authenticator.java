package io.aftersound.weave.security;

public interface Authenticator<REQUEST> {
    Authentication attemptAuthentication(REQUEST request);
}
