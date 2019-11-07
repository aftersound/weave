package io.aftersound.weave.service.security;

import io.aftersound.weave.security.AuthenticationControl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

class AuthenticationWrapper implements Authentication {

    private final AuthenticationControl authenticationControl;
    private final io.aftersound.weave.security.Authentication auth;

    public AuthenticationWrapper(
            AuthenticationControl authenticationControl,
            io.aftersound.weave.security.Authentication auth) {
        this.authenticationControl = authenticationControl;
        this.auth = auth;
    }

    public AuthenticationControl authenticationControl() {
        return authenticationControl;
    }

    public io.aftersound.weave.security.Authentication auth() {
        return auth;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return auth.getCredentials();
    }

    @Override
    public Object getDetails() {
        return auth.getDetails();
    }

    @Override
    public Object getPrincipal() {
        return auth.getPrincipal();
    }

    @Override
    public boolean isAuthenticated() {
        return auth.isAuthenticated();
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return auth.getName();
    }
}
