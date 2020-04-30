package io.aftersound.weave.config;

public class Tags {

    /**
     * Any {@link Key} with this tag means corresponding config
     * is security related
     */
    public static final String SECURITY = "_security_";

    /**
     * Any {@link Key} with this tag means corresponding config
     * is not only security related, but also needs to be protected
     */
    public static final String PROTECTED = "_protected_";
}
