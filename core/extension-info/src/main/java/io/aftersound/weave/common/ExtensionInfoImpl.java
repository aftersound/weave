package io.aftersound.weave.common;

public class ExtensionInfoImpl implements ExtensionInfo {

    /**
     * the name of the group this extension belongs to
     */
    private String group;

    /**
     * the name of this extension
     */
    private String name;

    /**
     * the version of this extension
     * it's recommended to use the version of the jar library
     */
    private String version;

    /**
     * the base type/class
     */
    private String baseType;

    /**
     * the type/class of this extension
     */
    private String type;

    /**
     * the jar location
     */
    private String jarLocation;

    @Override
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public String getBaseType() {
        return baseType;
    }

    public void setBaseType(String baseType) {
        this.baseType = baseType;
    }

    @Override
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getJarLocation() {
        return jarLocation;
    }

    public void setJarLocation(String jarLocation) {
        this.jarLocation = jarLocation;
    }

    @Override
    public String toString() {
        return "{" +
                "\"group\":" + "\"" + group + "\"," +
                "\"name\":" + "\"" + name + "\"," +
                "\"version\":" + "\"" + version + "\"," +
                "\"baseType\":" + "\"" + baseType + "\"," +
                "\"type\":" + "\"" + type + "\"," +
                "\"jarLocation\":" + "\"" + jarLocation + "\"," +
                "}";
    }

}
