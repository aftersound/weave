package io.aftersound.service.message;

public enum Category {

    SYSTEM("System"),
    APPLICATION("Application"),
    SERVICE("Service"),
    REQUEST("Request");

    private final String value;

    Category(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Category fromValue(String v) {
        for (Category c : Category.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
