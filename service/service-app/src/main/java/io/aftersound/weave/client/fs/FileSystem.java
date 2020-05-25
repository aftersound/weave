package io.aftersound.weave.client.fs;

import io.aftersound.weave.utils.PathHandle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileSystem {

    private final String baseDirectory;

    public FileSystem(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public InputStream getAsStream(String fileName) throws FileNotFoundException {
        File file = PathHandle.of(baseDirectory + "/" + fileName).path().toFile();
        return new FileInputStream(file);
    }

    public InputStream getAsStream(String relativeDirectory, String fileName) throws FileNotFoundException {
        File file = PathHandle.of(baseDirectory + "/" + relativeDirectory + "/" + fileName).path().toFile();
        return new FileInputStream(file);
    }
}
