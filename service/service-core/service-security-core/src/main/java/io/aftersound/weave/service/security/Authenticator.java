package io.aftersound.weave.service.security;

public interface Authenticator<CONTROL extends AuthenticationControl, BEARER> {
    Authentication authenticate(CONTROL control, BEARER tokenBearer) throws SecurityException;
}
