package io.aftersound.weave.couchbase;

import io.aftersound.weave.utils.Options;

import java.util.Map;

class Settings {

    private static final String NODES = "nodes";
    private static final String USER_NAME = "username";
    private static final String PASSWORD = "password";
    private static final String BUCKET = "bucket";

    private final Options options;

    private Settings(Options options) {
        this.options = options;
    }

    static Settings from(Map<String, Object> options) {
        return new Settings(Options.from(options));
    }

    String[] getNodes() {
        // Not worry about split performance here since it's not expected to
        // see frequent cluster connections
        return options.get(NODES,"").split(",");
    }

    String getUsername() {
        return options.get(USER_NAME);
    }

    String getPassword() {
        return options.get(PASSWORD);
    }

    String getBucket() {
        return options.get(BUCKET);
    }
}
