package io.aftersound.weave.service.config.cr;

import io.aftersound.weave.service.runtime.ConfigFormat;

class Util {

    static String getResourceName(String namespace, String configIdentifer, ConfigFormat configFormat) {
        return namespace + "/" + configIdentifer + (configFormat == ConfigFormat.Yaml ? ".yaml" : ".json");
    }

}
