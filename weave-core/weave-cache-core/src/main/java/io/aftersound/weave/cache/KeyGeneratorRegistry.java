package io.aftersound.weave.cache;

import java.util.HashMap;
import java.util.Map;

public class KeyGeneratorRegistry {

    private final Map<String, KeyGenerator> keyGeneratorByTypeName = new HashMap<>();

    public KeyGeneratorRegistry(Map<String, KeyGenerator> keyGeneratorByTypeName) {
        if (keyGeneratorByTypeName != null) {
            this.keyGeneratorByTypeName.putAll(keyGeneratorByTypeName);
        }
    }

    public KeyGenerator getKeyGenerator(String type) {
        return keyGeneratorByTypeName.get(type);
    }

}
