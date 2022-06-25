package io.aftersound.weave.common;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LibraryToolTest {

    @Test
    public void plain() throws Exception {
        final Path targetBasePath = Paths.get("").toAbsolutePath();

        LibraryTool libTool = new LibraryTool(System.getProperty("user.home") + "/.m2/repository");

        libTool.install(
                targetBasePath + "/plain/lib.list",
                targetBasePath + "/plain/weave-lib"
        );
    }

//    @Test
    public void b2380_f113() throws Exception {
        final Path targetBasePath = Paths.get("").toAbsolutePath();

        LibraryTool libTool = new LibraryTool(System.getProperty("user.home") + "/.m2/repository");

        libTool.install(
                targetBasePath + "/b2380_f113/lib.list",
                targetBasePath + "/b2380_f113/weave-lib"
        );
    }

}