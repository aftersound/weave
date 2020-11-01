package io.aftersound.weave.config;

import io.aftersound.weave.common.Key;

public interface Tags {

    /**
     * Any {@link Key} with this tag means corresponding config
     * is security related
     */
    String SECURITY = "_security_";

    /**
     * Any {@link Key} with this tag means corresponding config
     * is not only security related, but also needs to be protected
     */
    String PROTECTED = "_protected_";

    /**
     * Any {@link Key} with this tag means the key name is a pattern
     * could match one or more actual config keys.
     */
    String REGEX = "_regex_";
}
