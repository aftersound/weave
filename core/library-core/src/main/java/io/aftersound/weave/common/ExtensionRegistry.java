package io.aftersound.weave.common;

import java.util.List;

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
     * List all {@link ExtensionInfo} in this registry
     *
     * @return all {@link ExtensionInfo} in this registry
     */
    List<ExtensionInfo> list();

    /**
     * Get @{link ExtensionInfo} with given group, type and version.
     *
     * @param group   the type name of target extension
     * @param name    the name of target extension
     * @param version the expected version of target extension
     * @return detailed information of target extension
     */
    ExtensionInfo get(String group, String name, String version);

    /**
     * Get {@link ExtensionInfo}s with specified group and name from this registry
     *
     * @param group target group
     * @param name target name
     * @return {@link ExtensionInfo}s with specified group and name from this registry
     */
    List<ExtensionInfo> get(String group, String name);

    /**
     * Get {@link ExtensionInfo}s with specified group from this registry
     *
     * @param group target group
     * @return {@link ExtensionInfo}s with specified group from this registry
     */
    List<ExtensionInfo> get(String group);

}
