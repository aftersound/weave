package io.aftersound.weave.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {

    /**
     * Write bytes in specified file
     * @param bytes
     *          - file content in byte array
     * @param filePath
     *          - target file path, which may or may now has placeholders
     * @return complete and materialize Path of written file
     * @throws IOException
     */
    public static Path writeBytesInFile(byte[] bytes, String filePath) throws IOException {
        Path path = PathHandle.of(filePath).path();
        Files.createDirectories(path.getParent());
        if (Files.notExists(path)) {
            Files.createFile(path);
        }
        Files.write(path, bytes);
        return path;
    }

    public static boolean fileExists(String filePath) {
        Path path = PathHandle.of(filePath).path();
        return Files.exists(path);
    }
}
