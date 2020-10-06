package io.aftersound.weave.component;

public final class ComponentDestroyException extends RuntimeException {
    public ComponentDestroyException(String message, Throwable cause) {
        super(message, cause);
    }
}
