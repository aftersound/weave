package io.aftersound.weave.config;

public final class KeyFilters {

    public static KeyFilter keyWithTag(final String tag) {
        return new KeyFilter() {

            @Override
            public boolean isAcceptable(Key<?> key) {
                return key.hasTag(tag);
            }
        };
    }

    public static KeyFilter keyWithoutTag(final String tag) {
        return new KeyFilter() {

            @Override
            public boolean isAcceptable(Key<?> key) {
                return !key.hasTag(tag);
            }
        };
    }

    public static final KeyFilter SECURITY_KEY = keyWithTag(Tags.SECURITY);

    public static final KeyFilter NOT_SECURITY_KEY = keyWithoutTag(Tags.SECURITY);

}
