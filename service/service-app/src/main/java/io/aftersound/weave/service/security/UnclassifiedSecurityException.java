package io.aftersound.weave.service.security;

import org.springframework.security.core.AuthenticationException;

public class UnclassifiedSecurityException extends AuthenticationException {

    public UnclassifiedSecurityException(String msg, Throwable t) {
        super(msg, t);
    }

    public UnclassifiedSecurityException(String msg) {
        super(msg);
    }

}
