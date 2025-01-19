package io.aftersound.func;

/**
 * A ExecutionException is thrown when function execution fails due to some reason.
 */
public class ExecutionException extends RuntimeException {

    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExecutionException(String message) {
        super(message);
    }

}
