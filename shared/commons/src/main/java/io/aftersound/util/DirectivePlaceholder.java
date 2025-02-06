package io.aftersound.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

enum DirectivePlaceholder {

    UUID {

        @Override
        String value() {
            return java.util.UUID.randomUUID().toString();
        }

    },

    NOW_IN_MILLIS {

        @Override
        String value() {
            return String.valueOf(System.currentTimeMillis());
        }

    },

    NOW_IN_NANOS {

        @Override
        String value() {
            return String.valueOf(System.nanoTime());
        }

    },
    ;

    abstract String value();

    private static final Map<String, DirectivePlaceholder> BY_KEY;
    static {
        Map<String, DirectivePlaceholder> byKey = new HashMap<>();
        for (DirectivePlaceholder dp : DirectivePlaceholder.values()) {
            byKey.put(dp.name(), dp);
        }
        BY_KEY = Collections.unmodifiableMap(byKey);
    }

    public static DirectivePlaceholder withKey(String key) {
        return BY_KEY.get(key);
    }

}
