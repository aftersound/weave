package io.aftersound.weave.codec;

public class DecodeException extends RuntimeException {

    public DecodeException(String msg) {
        super(msg);
    }

    public DecodeException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
