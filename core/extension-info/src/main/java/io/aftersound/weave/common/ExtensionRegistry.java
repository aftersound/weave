package io.aftersound.weave.common;

/**
 * Conceptual entity which manages the registration of extension
 */
public interface ExtensionRegistry {

    /**
     * Register extension and persist the information to later usage
     *
     * @param extensionInfo
     */
    void register(ExtensionInfo extensionInfo);

    /**
     * Get extension info with given group, type and version.
     *
     * @param group   the type name of target extension
     * @param name    the name of target extension
     * @param version the expected version of target extension
     * @return detailed information of target extension
     */
    ExtensionInfo get(String group, String name, String version);
}
