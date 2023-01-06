package io.aftersound.weave.service.rl;

public final class RateLimitException extends Exception {

    public enum Code {
        NoRateLimitEvaluator
    }

    private final Code code;

    /**
     * Constructs an {@code RateLimitException} with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param t the root cause
     */
    private RateLimitException(Code code, String msg, Throwable t) {
        super(msg, t);
        this.code = code;
    }

    /**
     * Constructs an {@code RateLimitException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    private RateLimitException(Code code, String msg) {
        super(msg);
        this.code = code;
    }

    public static RateLimitException noRateLimitEvaluator(String type) {
        return new RateLimitException(Code.NoRateLimitEvaluator, "No rate limit evaluator for type " + type);
    }

    public Code getCode() {
        return code;
    }

}