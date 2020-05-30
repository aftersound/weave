package io.aftersound.weave.codec;

public class EncodeException extends RuntimeException {

    public EncodeException(String msg) {
        super(msg);
    }

    public EncodeException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
