package io.aftersound.weave.config;

public final class KeyFilters {

    public static final KeyFilter SECURITY_KEY = new KeyFilter() {

        @Override
        public boolean isAcceptable(Key<?> key) {
            return key.hasTag(Tags.SECURITY);
        }
    };

    public static final KeyFilter NOT_SECURITY_KEY = new KeyFilter() {

        @Override
        public boolean isAcceptable(Key<?> key) {
            return !key.hasTag(Tags.SECURITY);
        }
    };

}
