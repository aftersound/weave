package io.aftersound.weave.security;

public interface Authenticator<CONTROL extends AuthenticationControl, BEARER> {
    Authentication authenticate(CONTROL control, BEARER tokenBearer) throws SecurityException;
}
