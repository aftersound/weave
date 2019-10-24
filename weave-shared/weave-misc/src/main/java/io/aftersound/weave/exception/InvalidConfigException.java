package io.aftersound.weave.exception;

public class InvalidConfigException extends Exception {

    public InvalidConfigException(Throwable cause) {
        super(cause);
    }

    public InvalidConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidConfigException(String message) {
        super(message);
    }

}
