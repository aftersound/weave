package io.aftersound.util;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringHandle {

    private static final Pattern VAR = Pattern.compile("\\$\\{(.+?)\\}");

    private static final Map<String, String> EMPTY = Collections.emptyMap();

    private final String value;

    private StringHandle(String value) {
        this.value = value;
    }

    public static StringHandle of(String str, Map<String, String> placeholders) {
        return new StringHandle(normalize(str, placeholders));
    }

    public static StringHandle of(String str) {
        return StringHandle.of(str, EMPTY);
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
     *
     * Three types of placeholders are supported
     *  1.directives
     *  2.system properties
     *  3.environment variables
     *
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
        String pv = System.getProperty(placeholder);
        if (pv != null) {
            return pv;
        }

        return System.getenv(placeholder);
    }

    public String value() {
        return value;
    }
}
