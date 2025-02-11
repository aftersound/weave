package io.aftersound.config;


import io.aftersound.util.KeyFilter;
import io.aftersound.util.KeyFilterFactory;

import static io.aftersound.config.KeyAttributes.*;

public final class KeyFilters {

    public static final KeyFilter ANY = KeyFilterFactory.ANY;

    public static final KeyFilter SECRET_KEY = KeyFilterFactory.keyWithAttribute(SECRET, true);

    public static final KeyFilter NON_SECRET_KEY = key -> !key.hasAttribute(SECRET, true);

    public static final KeyFilter REQUIRED_KEY = KeyFilterFactory.keyWithAttribute(REQUIRED, true);

    public static final KeyFilter KEY_WITH_PATTERN = key -> key.getAttribute(PATTERN) != null;

}
