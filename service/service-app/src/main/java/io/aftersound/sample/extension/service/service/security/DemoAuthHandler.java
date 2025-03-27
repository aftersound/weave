package io.aftersound.sample.extension.service.service.security;

import io.aftersound.common.NamedType;
import io.aftersound.service.security.Auth;
import io.aftersound.service.security.AuthControl;
import io.aftersound.service.security.AuthHandler;
import io.aftersound.service.security.SecurityException;
import jakarta.ws.rs.container.ContainerRequestContext;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DemoAuthHandler extends AuthHandler<ContainerRequestContext> {

    public static final NamedType<AuthControl> COMPANION_CONTROL_TYPE = DemoAuthControl.TYPE;

    private static final MessageDigest MD;

    static {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA");
        } catch (NoSuchAlgorithmException e) {
            md = null;
        }
        MD = md;
    }

    @Override
    public String getType() {
        return COMPANION_CONTROL_TYPE.name();
    }

    @Override
    public Auth handle(
            AuthControl control,
            ContainerRequestContext bearer) throws SecurityException {

        String authToken = bearer != null ? bearer.getHeaderString("Authorization") : null;
        if (authToken == null) {
            throw SecurityException.missingTokenOrCredential();
        }

        if (!authToken.startsWith("Basic ")) {
            throw SecurityException.badCredential();
        }

        String decodedUserPassword;
        try {
            byte[] decoded = Base64.getDecoder().decode(authToken.substring("Basic ".length()));
            decodedUserPassword = new String(decoded, "UTF-8");
        } catch (Exception e) {
            throw SecurityException.badCredential();
        }

        int index = decodedUserPassword.indexOf(':');
        if (index < 0) {
            throw SecurityException.badCredential();
        }
        String user = decodedUserPassword.substring(0, index);
        String password = decodedUserPassword.substring(index + 1, decodedUserPassword.length());
        if (user.isEmpty() || password.isEmpty()) {
            throw SecurityException.badCredential();
        }

        DemoAuthControl authControl = (DemoAuthControl) control;

        String expectedPasswordHash = authControl.getUserCredentials().get(user);
        if (expectedPasswordHash == null) {
            throw SecurityException.badCredential();
        }

        String passwordHash = hashPassword(password);
        if (!expectedPasswordHash.equals(passwordHash)) {
            throw SecurityException.badCredential();
        }

        return new DemoAuth(user);

    }

    private String hashPassword(String password) throws SecurityException {
        if (MD == null) {
            throw SecurityException.authHandlingError(null);
        }
        try {
            MessageDigest md = (MessageDigest) MD.clone();
            md.update(password.getBytes());
            byte[] digest = md.digest();
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception e) {
            throw SecurityException.authHandlingError(e);
        }
    }

}