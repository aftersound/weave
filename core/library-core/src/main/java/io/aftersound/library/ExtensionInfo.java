package io.aftersound.library;

import java.util.Map;

public interface ExtensionInfo {
    String getGroup();

    String getName();

    String getVersion();

    String getBaseType();

    String getType();

    String getJarLocation();

    Map<String, String> asMap();
}
