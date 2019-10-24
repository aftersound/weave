package io.aftersound.weave.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHandle {

    private static final Pattern VAR = Pattern.compile("\\$\\{(.+?)\\}");

    private final String value;

    private StringHandle(String value) {
        this.value = value;
    }

    public static StringHandle of(String str, Map<String, String> placeholders) {
        return new StringHandle(normalize(str, placeholders));
    }

    /**
     * Normalize and transform string.
     * ${PLACEHOLDER} in the string will be replaced with certain value
     * Note: performance is not that good, but enough for non intensive cases
     * @param str
     *          - input String, which may or may not have placeholders
     * @return normalized String
     */
    private static String normalize(String str, Map<String, String> placeholderValues) {
        if (str == null) {
            return null;
        }

        String p = str.trim();
        if (p.isEmpty()) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        Matcher m = VAR.matcher(p);
        while (m.find()) {
            String placeholder = m.group(1);
            String value = getPlaceholderValue(placeholder, placeholderValues);
            m.appendReplacement(sb, value != null ? value : "");
        }
        m.appendTail(sb);
        return sb.toString();
    }

    /**
     * Get value of specified placeholder.
     * Two types of placeholders are supported: directives and system properties
     * @param placeholder
     *          name of placeholder
     * @param placeholderValues
     *          map of placeholder/value, which may or may not contain value of
     *          every expected placeholder
     * @return
     *          String value of placeholder
     */
    private static String getPlaceholderValue(String placeholder, Map<String, String> placeholderValues) {
        // 1.try given placeholder values
        if (placeholderValues.containsKey(placeholder)) {
            return placeholderValues.get(placeholder);
        }

        // 2.try directive placeholder
        DirectivePlaceholder dp = DirectivePlaceholder.withKey(placeholder);
        if (dp != null) {
            return dp.value();
        }

        // 3.try system properties
        return System.getProperty(placeholder);
    }

    public String value() {
        return value;
    }
}
