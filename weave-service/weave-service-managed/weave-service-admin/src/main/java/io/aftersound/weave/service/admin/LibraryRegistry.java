package io.aftersound.weave.service.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

class LibraryRegistry {

    private Map<String, LibraryInfo> libraryById = new HashMap<>();

    public void addLibrary(String groupId, String artifactId, String version, LibraryInfo libraryInfo) {
        libraryById.put(libraryId(groupId, artifactId, version), libraryInfo);
    }

    public LibraryInfo getLibrary(String groupId, String artifactId, String version) {
        return libraryById.get(libraryId(groupId, artifactId, version));
    }

    public LibraryInfo removeLibrary(String groupId, String artifactId, String version) {
        return libraryById.remove(libraryId(groupId, artifactId, version));
    }

    public Collection<LibraryInfo> getLibraries() {
        return libraryById.values();
    }

    public Collection<LibraryInfo> findLibraries(String groupId, String artifactId, String version) {
        Collection<LibraryInfo> targets = new ArrayList<>();
        for (String libraryId : libraryById.keySet()) {
            if (match(libraryId, groupId, artifactId, version)) {
                targets.add(libraryById.get(libraryId));
            }

        }
        return targets;
    }

    private String libraryId(String groupId, String artifactId, String version) {
        return groupId + ":" + artifactId + ":" + version;
    }

    private static boolean match(String libraryId, String groupId, String artifactId, String version) {
        String[] keys = libraryId.split(":");
        return  (groupId == null || groupId.isEmpty() || groupId.equals("*") || groupId.equals(keys[0]))
                &&
                (artifactId == null || artifactId.isEmpty() || artifactId.equals("*") || artifactId.equals(keys[1]))
                &&
                (version == null || version.isEmpty() || version.equals("*") || version.equals(keys[2]));
    }

}
