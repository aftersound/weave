package io.aftersound.weave.service.admin;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class ExtensionLibraryRegistryTest {

    @Test
    public void addLibrary() {
    }

    @Test
    public void getLibrary() {
    }

    @Test
    public void removeLibrary() {
    }

    @Test
    public void getLibraries() {
    }

    @Test
    public void findLibraries() {
        LibraryRegistry registry = new LibraryRegistry();

        LibraryInfo l1 = new LibraryInfo();
        l1.setPath("path1");
        l1.setDescription(new HashMap<String, String>());
        l1.getDescription().put("groupId", "io.aftersound.weave");
        l1.getDescription().put("artifactId", "weave-service-core");
        l1.getDescription().put("version", "1.0.0");
        registry.addLibrary(
                "io.aftersound.weave",
                "weave-service-core",
                "1.0.0",
                l1);

        LibraryInfo l2 = new LibraryInfo();
        l2.setPath("path1");
        l2.setDescription(new HashMap<String, String>());
        l2.getDescription().put("groupId", "io.aftersound.weave");
        l2.getDescription().put("artifactId", "weave-batch-core");
        l2.getDescription().put("version", "1.0.1");
        registry.addLibrary(
                "io.aftersound.weave",
                "weave-batch-core",
                "1.0.1",
                l2);

        assertEquals(
                2,
                registry.findLibraries("io.aftersound.weave", null, null).size()
        );

        assertEquals(
                1,
                registry.findLibraries(null, "weave-batch-core", null).size()
        );

        assertEquals(
                1,
                registry.findLibraries(null, null, "1.0.0").size()
        );

        assertEquals(
                "weave-service-core",
                registry.findLibraries(null, null, "1.0.0").iterator().next().getDescription().get("artifactId")
        );

    }
}