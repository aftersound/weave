package io.aftersound.weave.security;

public interface Authenticator<CONTROL extends AuthenticationControl> {
    Authentication attemptAuthentication(CONTROL control, String bearer);
}
