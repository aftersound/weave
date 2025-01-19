package io.aftersound.func;

/**
 * A CreationException is thrown when function creation fails due to some problem.
 */
public class CreationException extends RuntimeException {

    public CreationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CreationException(String message) {
        super(message);
    }

}
