package io.aftersound.weave.common;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileBasedExtensionRegistry implements ExtensionRegistry {

    private final Path directory;
    private final Path file;

    public FileBasedExtensionRegistry(String directory, String file) {
        Path dir = Paths.get(directory);
        Path f = Paths.get(directory, file);
        assert Files.isDirectory(dir) && Files.exists(f) && Files.isRegularFile(f);

        this.directory = dir;
        this.file = f;
    }

    @Override
    public void register(ExtensionInfo extensionInfo) {

    }

    @Override
    public ExtensionInfo get(String group, String name, String version) {
        return null;
    }

}
