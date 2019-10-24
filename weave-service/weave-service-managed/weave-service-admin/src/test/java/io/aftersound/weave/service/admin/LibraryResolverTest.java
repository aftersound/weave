package io.aftersound.weave.service.admin;

import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.*;

public class LibraryResolverTest {

    @Test
    public void testIBiblioResolver() throws Exception {
        Path libraryPath = new LibraryResolver(
                "maven://localhost:8081/nexus/content/repositories/snapshots"
        ).resolve(
                "io.aftersound.weave",
                "weave-service-shared",
                "0.0.1-SNAPSHOT"
        );
        assertNotNull(libraryPath);
        assertTrue(libraryPath.toString().endsWith(".jar"));
        assertFalse(libraryPath.toString().endsWith("-sources.jar"));
    }

}