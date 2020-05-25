package io.aftersound.weave.utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Map;

public class PathHandle {

    private static final Map<String, String> EMPTY = Collections.emptyMap();

    private final Path path;

    private PathHandle(Path path) {
        this.path = path;
    }

    /**
     * Create a handle for path in string
     * @param path
     *          - path in string, which may or may not have placeholders
     * @return PathHandle
     */
    public static PathHandle of(String path) {
        return new PathHandle(normalize(path));
    }

    /**
     * Normalize and transform path in string.
     * ${PLACEHOLDER} in the string will be replaced with certain value
     * @param pathStr
     *          - path in string, which may or may now have placeholders
     * @return Path
     */
    private static Path normalize(String pathStr) {
        String normalized = StringHandle.of(pathStr, EMPTY).value();
        if (normalized == null || normalized.isEmpty()) {
            return null;
        }
        return Paths.get(normalized);
    }

    /**
     * @return Path which this handle holds
     */
    public Path path() {
        return path;
    }

}
