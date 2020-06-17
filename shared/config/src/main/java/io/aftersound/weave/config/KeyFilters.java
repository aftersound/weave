package io.aftersound.weave.config;

import io.aftersound.weave.common.CommonKeyFilters;
import io.aftersound.weave.common.KeyFilter;

public final class KeyFilters extends CommonKeyFilters {

    public static final KeyFilter SECURITY_KEY = keyWithTag(Tags.SECURITY);

    public static final KeyFilter NOT_SECURITY_KEY = keyWithoutTag(Tags.SECURITY);

}
