package io.aftersound.weave.common;

public class CommonKeyFilters {

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

    public static KeyFilter keyWithPrefix(final String prefix) {
        return new KeyFilter() {

            @Override
            public boolean isAcceptable(Key<?> key) {
                return key.name().startsWith(prefix);
            }
        };
    }

    public static final KeyFilter ANY = new KeyFilter() {

        @Override
        public boolean isAcceptable(Key<?> key) {
            return true;
        }

    };

}
