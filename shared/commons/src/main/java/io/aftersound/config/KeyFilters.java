package io.aftersound.config;


import io.aftersound.util.CommonKeyFilters;
import io.aftersound.util.KeyFilter;

public final class KeyFilters extends CommonKeyFilters {

    public static final KeyFilter SECURITY_KEY = keyWithTag(Tags.SECURITY);

    public static final KeyFilter NOT_SECURITY_KEY = keyWithoutTag(Tags.SECURITY);

}
