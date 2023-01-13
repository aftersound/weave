package io.aftersound.weave.common;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FileBasedExtensionRegistryTest {

    private static String dir = System.getProperty("java.io.tmpdir");
    private static String file = "FileBasedExtensionRegistryTest.json";

    private static ExtensionRegistry extensionRegistry;

    @BeforeClass
    public static void setup() throws Exception {
        try (InputStream is = FileBasedExtensionRegistryTest.class.getResourceAsStream("/_extensions.json")) {
            Files.copy(
                    is,
                    Paths.get(dir, file),
                    StandardCopyOption.REPLACE_EXISTING);
        }

        extensionRegistry = new FileBasedExtensionRegistry(dir, file);
    }

    @Test
    public void register() {
        ExtensionInfo ei = new ExtensionInfo() {
            @Override
            public String getGroup() {
                return "COMPONENT_FACTORY";
            }

            @Override
            public String getName() {
                return "ActorRegistry";
            }

            @Override
            public String getVersion() {
                return "0.0.2-SNAPSHOT";
            }

            @Override
            public String getBaseType() {
                return "io.aftersound.weave.component.ComponentFactory";
            }

            @Override
            public String getType() {
                return "io.aftersound.weave.component.ActorRegistryFactory";
            }

            @Override
            public String getJarLocation() {
                return "/opt/weave/lib/service/io.aftersound.weave__component-registry__0.0.2-SNAPSHOT__component-registry-0.0.2-SNAPSHOT.jar";
            }

            @Override
            public Map<String, String> asMap() {
                return null;
            }
        };
        extensionRegistry.register(ei);

        extensionRegistry.get("COMPONENT_FACTORY", "ActorRegistry", "0.0.2-SNAPSHOT");
        assertNotNull(ei);
        assertEquals("/opt/weave/lib/service/io.aftersound.weave__component-registry__0.0.2-SNAPSHOT__component-registry-0.0.2-SNAPSHOT.jar", ei.getJarLocation());
    }

    @Test
    public void list() {
        List<ExtensionInfo> extensionInfoList = extensionRegistry.list();
        assertEquals(51, extensionInfoList.size());
    }

    @Test
    public void get() {
        ExtensionInfo ei = extensionRegistry.get("COMPONENT_FACTORY", "ActorRegistry", "0.0.1-SNAPSHOT");
        assertNotNull(ei);
        assertEquals("/opt/weave/lib/service/io.aftersound.weave__component-registry__0.0.1-SNAPSHOT__component-registry-0.0.1-SNAPSHOT.jar", ei.getJarLocation());

        List<ExtensionInfo> extensionInfoList = extensionRegistry.get("COMPONENT_FACTORY", "ActorRegistry");
        assertEquals(2, extensionInfoList.size());

        extensionInfoList = extensionRegistry.get("COMPONENT_FACTORY");
        assertEquals(19, extensionInfoList.size());
    }

}