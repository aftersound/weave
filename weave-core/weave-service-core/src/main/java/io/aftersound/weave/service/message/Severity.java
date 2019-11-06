package io.aftersound.weave.service.message;

public enum Severity {

    ERROR("Error"),
    WARNING("Warning");

    private final String value;

    Severity(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Severity fromValue(String v) {
        for (Severity s : Severity.values()) {
            if (s.value.equals(v)) {
                return s;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
