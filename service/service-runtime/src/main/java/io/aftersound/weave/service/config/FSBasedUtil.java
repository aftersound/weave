package io.aftersound.weave.service.config;

import io.aftersound.weave.service.runtime.ConfigFormat;

class FSBasedUtil {

    static String getFileName(String configIdentifier, ConfigFormat configFormat) {
        return configIdentifier + (configFormat == ConfigFormat.Yaml ? ".yaml" : ".json");
    }

}
