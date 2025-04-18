package io.aftersound.service.security;

public final class SecurityException extends Exception {

    public enum Code {
        Unclassified,
        NoAuthHandler,
        MissingTokenOrCredential,
        BadToken,
        TokenExpired,
        BadCredential,
        CredentialsExpired,
        AuthHandlingError,
        AccessDenied
    }

    private final Code code;

    /**
     * Constructs an {@code SecurityException} with the specified message and root
     * cause.
     *
     * @param msg the detail message
     * @param t the root cause
     */
    private SecurityException(Code code, String msg, Throwable t) {
        super(msg, t);
        this.code = code;
    }

    /**
     * Constructs an {@code SecurityException} with the specified message and no
     * root cause.
     *
     * @param msg the detail message
     */
    private SecurityException(Code code, String msg) {
        super(msg);
        this.code = code;
    }

    public static SecurityException unclassifiable(Throwable cause) {
        return new SecurityException(Code.Unclassified, "Unclassified security exception", cause);
    }

    public static SecurityException noAuthHandler(String type) {
        return new SecurityException(Code.NoAuthHandler, "No handler for type " + type);
    }

    public static SecurityException missingTokenOrCredential() {
        return new SecurityException(Code.MissingTokenOrCredential, "Missing token or credential");
    }

    public static SecurityException badToken() {
        return new SecurityException(Code.BadToken, "Invalid token");
    }

    public static SecurityException tokenExpired() {
        return new SecurityException(Code.TokenExpired, "Token is expired");
    }

    public static SecurityException badCredential() {
        return new SecurityException(Code.BadCredential, "Invalid credential");
    }

    public static SecurityException credentialsExpired() {
        return new SecurityException(Code.CredentialsExpired, "Credential is expired");
    }

    public static SecurityException accessDenied() {
        return new SecurityException(Code.AccessDenied, "Access is denied");
    }

    public static SecurityException authHandlingError(Throwable e) {
        return new SecurityException(Code.AuthHandlingError, "Auth handling internal error", e);
    }

    public Code getCode() {
        return code;
    }

}