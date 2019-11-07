package io.aftersound.weave.security;

public interface Authorizer<CONTROL extends AuthorizationControl> {
    Authorization authorize(CONTROL control, Authentication authentication) throws SecurityException;
}
