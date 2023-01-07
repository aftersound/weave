package io.aftersound.weave.library;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.common.ExtensionInfoImpl;
import io.aftersound.weave.maven.MavenHelper;
import io.aftersound.weave.maven.Resolution;
import io.aftersound.weave.utils.MapBuilder;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LibraryManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryManagement.class);

    private final MavenHelper mavenHelper;
    private final String workDir;

    public LibraryManagement(MavenHelper mavenHelper, String dockerSourceDirectory) {
        this.mavenHelper = mavenHelper;
        this.workDir = Paths.get("").toAbsolutePath() + "/" + dockerSourceDirectory;

        File file = new File(workDir);
        assert (file.exists() && file.isDirectory()) : (workDir + " does not exist or is not directory");

        try {
            File weaveLibDir = new File(workDir + "/weave/lib");
            if (!weaveLibDir.exists()) {
                weaveLibDir.mkdir();
            }

            File beamLibDir = new File(workDir + "/weave/lib/beam");
            if (beamLibDir.isDirectory() && beamLibDir.exists()) {
                FileUtils.deleteDirectory(beamLibDir);
            }
            beamLibDir.mkdir();

            File serviceLibDir = new File(workDir + "/weave/lib/service");
            if (serviceLibDir.isDirectory() && serviceLibDir.exists()) {
                FileUtils.deleteDirectory(serviceLibDir);
            }
            serviceLibDir.mkdir();
        } catch (IOException ioe) {
        }
    }

    public LibraryManagement(String localMavenRepository, String dockerSourceDirectory) {
        this(new MavenHelper(localMavenRepository), dockerSourceDirectory);
    }

    public LibraryManagement(String dockerDirectory) {
        this(new MavenHelper(), dockerDirectory);
    }

    public void executeFor(Target target) throws Exception {
        processServiceLibraries(target);
        processBeamLibraries(target);
    }

    private void processServiceLibraries(Target target) throws Exception {
        final List<Map<String, Object>> sourceLibList = getLibraryInfoList(workDir + "/service-lib-list.json");
        final String libDir = workDir + "/weave/lib/service";
        final String libDirInList;
        switch (target) {
            case Docker:
            {
                libDirInList = "/opt/weave/lib/service";   // the path in docker container;
                break;
            }
            case LocalDevelopment:
            default:
            {
                libDirInList = libDir;
                break;
            }

        }

        ensureMavenArtifacts(sourceLibList);
        processLibraries(sourceLibList, libDir, libDirInList);
    }

    private void processBeamLibraries(Target target) throws Exception {
        final List<Map<String, Object>> sourceLibList = getLibraryInfoList(workDir + "/beam-lib-list.json");
        final String libDir = workDir + "/weave/lib/beam";
        final String libDirInList;
        switch (target) {
            case Docker:
            {
                libDirInList = "/opt/weave/lib/beam";   // the path in docker container;
                break;
            }
            case LocalDevelopment:
            default:
            {
                libDirInList = libDir;
                break;
            }
        }

        ensureMavenArtifacts(sourceLibList);
        processLibraries(sourceLibList, libDir, libDirInList);
    }

    private List<Map<String, Object>> getLibraryInfoList(String file) throws Exception{
        try (InputStream is = new FileInputStream(file)) {
            return new ObjectMapper().readValue(is, new TypeReference<List<Map<String, Object>>>() {});
        }
    }

    private void ensureMavenArtifacts(List<Map<String, Object>> mavenArtifacts) throws Exception {
        Resolution resolution = mavenHelper.resolveMavenArtifacts(mavenArtifacts);
        if (resolution.getUnresolved().size() > 0) {
            throw new RuntimeException("Below artifacts are not resolved:\n\t" + resolution.getResolved());
        }
    }

    private void processLibraries(
            final List<Map<String, Object>> libInfoList,
            final String libDir,
            final String libDirForList) throws Exception {

        final MavenHelper.Action copyTo = new CopyTo(libDir);
        final LibraryListGenerator libraryListGenerator = new LibraryListGenerator(libDirForList);

        mavenHelper.findAndExec(libInfoList, new MavenHelper.CompositeAction(copyTo, libraryListGenerator));

        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_jar-name.list"),
                toString(libraryListGenerator.getJarNameList()).getBytes(StandardCharsets.UTF_8)
        );
        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_jar-file.list"),
                toString(libraryListGenerator.getJarFileList()).getBytes(StandardCharsets.UTF_8)
        );
        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_library.json"),
                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(libraryListGenerator.getJarInfoList())
        );
        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_library-slim.json"),
                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(libraryListGenerator.getSlimJarInfoList())
        );
        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_extensions.json"),
                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(libraryListGenerator.getExtensionInfoList())
        );
        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_extension_groups.json"),
                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(libraryListGenerator.getExtensionGroups())
        );
    }

    private String toString(List<?> list) {
        StringJoiner joiner = new StringJoiner("\n");
        for (Object e : list) {
            joiner.add(e.toString());
        }
        return joiner.toString();
    }

    private String toPrettyString(List<?> list, boolean withDoubleQuote) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (Object e : list) {
            if (first) {
                first = false;
            } else {
                sb.append(",\n");
            }
            if (withDoubleQuote) {
                sb.append("\"").append(e).append("\"");
            } else {
                sb.append(e);
            }
        }
        return sb.toString();
    }

    public static class CopyTo implements MavenHelper.Action {

        private final File targetLocation;

        public CopyTo(String targetDirectory) {
            File targetLocation = new File(targetDirectory);
            assert targetLocation.exists() && targetLocation.isDirectory() : (targetDirectory + " is not a directory");
            this.targetLocation = targetLocation;
        }

        @Override
        public void act(String file, Map<String, Object> libraryInfo) throws Exception {
            final String groupId = (String) libraryInfo.get("groupId");
            final String version = (String) libraryInfo.get("version");
            final String artifactId = (String) libraryInfo.get("artifactId");
            final String artifactFileName = (String) libraryInfo.get("artifactFileName");

            final String targetFileName = groupId + "__" + artifactId + "__" + version + "__" + artifactFileName;
            final File targetFile = new File(targetLocation.getPath(), targetFileName);
            LOGGER.info("Copy file from '{}' to '{}'", file, targetFile);
            FileUtils.copyFile(new File(file), targetFile);
        }
    }

    private static class LibraryListGenerator implements MavenHelper.Action {

        private static final ObjectMapper MAPPER = new ObjectMapper();

        private static final String[] LIB_INFO_KEYS = {"groupId", "artifactId", "version", "jarLocation", "tags"};
        private static final String[] SLIM_LIB_INFO_KEYS = {"groupId", "artifactId", "version"};

        private final List<String> jarNameList = new LinkedList<>();
        private final List<String> jarFilelist = new LinkedList<>();
        private final List<Map<String, Object>> jarInfoList = new LinkedList<>();
        private final List<Map<String, Object>> slimJarInfoList = new LinkedList<>();
        private final List<ExtensionInfoImpl> extensionInfoList = new LinkedList<>();

        private final String baseDir;

        public LibraryListGenerator(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void act(String file, Map<String, Object> libraryInfo)  {
            final String groupId = (String) libraryInfo.get("groupId");
            final String version = (String) libraryInfo.get("version");
            final String artifactId = (String) libraryInfo.get("artifactId");
            final String artifactFileName = (String) libraryInfo.get("artifactFileName");

            final String targetFileName = baseDir + "/" + groupId + "__" + artifactId + "__" + version + "__" + artifactFileName;

            jarNameList.add(artifactFileName);
            jarFilelist.add(targetFileName);

            jarInfoList.add(
                    MapBuilder.linkedHashMap()
                            .keys(LIB_INFO_KEYS)
                            .values(groupId, artifactId, version, targetFileName, libraryInfo.get("tags"))
                            .build()
            );

            slimJarInfoList.add(
                    MapBuilder.linkedHashMap()
                            .keys(SLIM_LIB_INFO_KEYS)
                            .values(groupId, artifactId, version)
                            .build()
            );

            extractExtensionInfos(file, version, targetFileName);
        }

        private void extractExtensionInfos(String jarFile, String version, String targetFileName) {
            ZipFile zipFile = null;
            try {
                zipFile = new ZipFile(new File(jarFile));
                ZipEntry zipEntry = zipFile.getEntry("META-INF/weave/extensions.json");
                if (zipEntry != null) {
                    List<ExtensionInfoImpl> eiList = MAPPER.readValue(
                            zipFile.getInputStream(zipEntry),
                            new TypeReference<List<ExtensionInfoImpl>>() {}
                    );
                    for (ExtensionInfoImpl ei : eiList) {
                        ei.setVersion(version);
                        ei.setJarLocation(targetFileName);
                    }
                    extensionInfoList.addAll(eiList);
                }
            } catch (Exception e) {
                LOGGER.error("Failed to get and parse extensions.json from {}", jarFile, e);
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (Exception e) {
                    }
                }
            }
        }

        public List<String> getJarNameList() {
            Collections.sort(jarNameList, Comparator.naturalOrder());
            return Collections.unmodifiableList(jarNameList);
        }

        public List<String> getJarFileList() {
            Collections.sort(jarFilelist, Comparator.naturalOrder());
            return Collections.unmodifiableList(jarFilelist);
        }

        public List<Map<String, Object>> getJarInfoList() {
            Collections.sort(jarInfoList, ArtifactComparator.INSTANCE);
            return Collections.unmodifiableList(jarInfoList);
        }

        public List<Map<String, Object>> getSlimJarInfoList() {
            Collections.sort(slimJarInfoList, ArtifactComparator.INSTANCE);
            return Collections.unmodifiableList(slimJarInfoList);
        }

        public List<ExtensionInfoImpl> getExtensionInfoList() {
            Collections.sort(extensionInfoList, ExtensionInfoImplComparator.INSTANCE);
            return Collections.unmodifiableList(extensionInfoList);
        }

        public List<Map<String, Object>> getExtensionGroups() {
            Map<String, Map<String, Object>> byGroup = new LinkedHashMap<>();
            for (ExtensionInfoImpl ei : extensionInfoList) {
                if (byGroup.get(ei.getGroup()) == null) {
                    byGroup.put(
                            ei.getGroup(),
                            MapBuilder.linkedHashMap()
                                    .kv("group", ei.getGroup())
                                    .kv("baseType", ei.getBaseType())
                                    .kv("types", new LinkedList<>())
                                    .build()
                    );
                }
                Map<String, Object> eg = byGroup.get(ei.getGroup());
                List<String> types = (List<String>) eg.get("types");
                types.add(ei.getType());
            }

            for (Map<String, Object> eg : byGroup.values()) {
                List<String> types = (List<String>) eg.get("types");
                Collections.sort(types);
            }

            return new ArrayList<>(byGroup.values());
        }

    }

    private static class ArtifactComparator implements Comparator<Map<String, Object>> {

        public static final ArtifactComparator INSTANCE = new ArtifactComparator();

        @Override
        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
            return id(o1).compareTo(id(o2));
        }

        private String id(Map<String, Object> artifact) {
            final String groupId = (String) artifact.get("groupId");
            final String artifactId = (String) artifact.get("artifactId");
            final String version = (String) artifact.get("version");
            final String artifactName = artifactId + "-" + version;
            return groupId + ":" + artifactId + ":" + version + ":" + artifactName;
        }

    }

    private static class ExtensionInfoImplComparator implements Comparator<ExtensionInfoImpl> {

        public static final ExtensionInfoImplComparator INSTANCE = new ExtensionInfoImplComparator();

        @Override
        public int compare(ExtensionInfoImpl o1, ExtensionInfoImpl o2) {
            return id(o1).compareTo(id(o2));
        }

        private String id(ExtensionInfoImpl ei) {
            return ei.getGroup() + ":" + ei.getName() + ":" + ei.getBaseType() + ":" + ei.getType();
        }

    }

}
