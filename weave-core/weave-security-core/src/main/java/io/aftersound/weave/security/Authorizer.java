package io.aftersound.weave.security;

public interface Authorizer {
    Authorization attemptAuthorization(Authentication authentication);
}
