package io.aftersound.weave.service.security;

public class SecurityControl  {

    private AuthenticationControl authenticationControl;
    private AuthorizationControl authorizationControl;

    public AuthenticationControl getAuthenticationControl() {
        return authenticationControl;
    }

    public void setAuthenticationControl(AuthenticationControl authenticationControl) {
        this.authenticationControl = authenticationControl;
    }

    public AuthorizationControl getAuthorizationControl() {
        return authorizationControl;
    }

    public void setAuthorizationControl(AuthorizationControl authorizationControl) {
        this.authorizationControl = authorizationControl;
    }
}
