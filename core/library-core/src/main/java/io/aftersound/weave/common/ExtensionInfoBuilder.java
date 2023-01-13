package io.aftersound.weave.common;

public class ExtensionInfoBuilder {

    static ExtensionInfo extensionInfo(String group, String name, String version, String baseType, String type, String jarLocation) {
        ExtensionInfoImpl e = new ExtensionInfoImpl();
        e.setGroup(group);
        e.setName(name);
        e.setVersion(version);
        e.setBaseType(baseType);
        e.setType(type);
        e.setJarLocation(jarLocation);
        return e;
    }

}
