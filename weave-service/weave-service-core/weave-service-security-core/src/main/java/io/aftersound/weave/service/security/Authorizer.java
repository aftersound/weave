package io.aftersound.weave.service.security;

public interface Authorizer<CONTROL extends AuthorizationControl> {
    Authorization authorize(CONTROL control, Authentication authentication) throws SecurityException;
}
