package io.aftersound.weave.service.admin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

class Result {

    static final Map<String, String> SUCCESS;
    static final Map<String, String> FAILURE;

    static {
        Map<String, String> success = new HashMap<>();
        success.put("status", "success");
        SUCCESS = Collections.unmodifiableMap(success);

        Map<String, String> failure = new HashMap<>();
        failure.put("status", "failure");
        FAILURE = Collections.unmodifiableMap(failure);
    }
}
