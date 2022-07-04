package io.aftersound.weave.docker;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    public void execute() throws Exception {
        processServiceLibraries();
        processBeamLibraries();
    }

    private void processServiceLibraries() throws Exception {
        final List<Map<String, String>> sourceLibList = getLibraryInfoList(workDir + "/service-lib-list.json");
        final String libDir = workDir + "/weave/lib/service";
        final String libDirInList = "/opt/weave/lib/service";   // the path in docker container

        ensureMavenArtifacts(sourceLibList);
        processLibraries(sourceLibList, libDir, libDirInList);
    }

    private void processBeamLibraries() throws Exception {
        final List<Map<String, String>> sourceLibList = getLibraryInfoList(workDir + "/beam-lib-list.json");
        final String libDir = workDir + "/weave/lib/beam";
        final String libDirInList = "/opt/weave/lib/beam";   // the path in docker container

        ensureMavenArtifacts(sourceLibList);
        processLibraries(sourceLibList, libDir, libDirInList);
    }

    private List<Map<String, String>> getLibraryInfoList(String file) throws Exception{
        try (InputStream is = new FileInputStream(file)) {
            return new ObjectMapper().readValue(is, new TypeReference<List<Map<String, String>>>() {});
        }
    }

    private void ensureMavenArtifacts(List<Map<String, String>> mavenArtifacts) throws Exception {
        Resolution resolution = mavenHelper.resolveMavenArtifacts(mavenArtifacts);
        if (resolution.getUnresolved().size() > 0) {
            throw new RuntimeException("Below artifacts are not resolved:\n\t" + resolution.getResolved());
        }
    }

    private void processLibraries(
            final List<Map<String, String>> libInfoList,
            final String libDir,
            final String libDirForList) throws Exception {

        final MavenHelper.Action copyTo = new CopyTo(libDir);
        final LibraryListGenerator libraryListGenerator = new LibraryListGenerator(libDirForList);

        mavenHelper.findAndExec(libInfoList, new MavenHelper.CompositeAction(copyTo, libraryListGenerator));

        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_jar-name.list"),
                toPrettyString(libraryListGenerator.getJarNameList(), true).getBytes(StandardCharsets.UTF_8)
        );
        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_jar-file.list"),
                toPrettyString(libraryListGenerator.getJarFileList(), true).getBytes(StandardCharsets.UTF_8)
        );
        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_library.json"),
                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(libraryListGenerator.getJarInfoList())
        );
        FileUtils.writeByteArrayToFile(
                new File(libDir + "/_library-slim.json"),
                new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsBytes(libraryListGenerator.getSlimJarInfoList())
        );
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

    public static class CopyTo extends MavenHelper.Action {

        private final File targetLocation;

        public CopyTo(String targetDirectory) {
            File targetLocation = new File(targetDirectory);
            assert targetLocation.exists() && targetLocation.isDirectory() : (targetDirectory + " is not a directory");
            this.targetLocation = targetLocation;
        }

        @Override
        public void act(File file, Map<String, String> libraryInfo) throws Exception {
            final String groupId = libraryInfo.get("groupId");
            final String version = libraryInfo.get("version");
            final String artifactId = libraryInfo.get("artifactId");
            final String artifactFileName = libraryInfo.get("artifactFileName");

            final String targetFileName = groupId + "__" + artifactId + "__" + version + "__" + artifactFileName;
            final File targetFile = new File(targetLocation.getPath(), targetFileName);
            LOGGER.info("Copy file from '{}' to '{}'", file.toString(), targetFile.toString());
            FileUtils.copyFile(file, targetFile);
        }
    }

    private static class LibraryListGenerator extends MavenHelper.Action {

        private static final String[] LIB_INFO_KEYS = {"groupId", "artifactId", "version", "jarLocation"};
        private static final String[] SLIM_LIB_INFO_KEYS = {"groupId", "artifactId", "version"};

        private final List<String> jarFilelist = new ArrayList<>(100);
        private final List<Map<String, String>> jarInfoList = new ArrayList<>(100);
        private final List<Map<String, String>> slimJarInfoList = new ArrayList<>(100);

        private final String baseDir;

        public LibraryListGenerator(String baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void act(File file, Map<String, String> libraryInfo)  {
            final String groupId = libraryInfo.get("groupId");
            final String version = libraryInfo.get("version");
            final String artifactId = libraryInfo.get("artifactId");
            final String artifactFileName = libraryInfo.get("artifactFileName");

            final String targetFileName = baseDir + "/" + groupId + "__" + artifactId + "__" + version + "__" + artifactFileName;

            jarFilelist.add(targetFileName);

            jarInfoList.add(
                    MapBuilder.linkedHashMap()
                            .keys(LIB_INFO_KEYS)
                            .values(groupId, artifactId, version, targetFileName)
                            .build()
            );

            slimJarInfoList.add(
                    MapBuilder.linkedHashMap()
                            .keys(SLIM_LIB_INFO_KEYS)
                            .values(groupId, artifactId, version)
                            .build()
            );
        }

        public List<String> getJarFileList() {
            return Collections.unmodifiableList(jarFilelist);
        }

        public List<Map<String, String>> getJarInfoList() {
            return Collections.unmodifiableList(jarInfoList);
        }

        public List<Map<String, String>> getSlimJarInfoList() {
            return Collections.unmodifiableList(slimJarInfoList);
        }

    }

    private static class ForServiceTestBeamLibraryListGenerator extends MavenHelper.Action {

        private final List<String> jarFileList = new ArrayList<>(100);
        private final List<Map<String, String>> jarInfoList = new ArrayList<>(100);

        @Override
        public void act(File file, Map<String, String> libraryInfo) {
            final String jarLocation = file.toString();
            jarFileList.add(jarLocation);

            Map<String, String> m = new LinkedHashMap<>(5);
            m.put("groupId", libraryInfo.get("groupId"));
            m.put("artifactId", libraryInfo.get("artifactId"));
            m.put("version", libraryInfo.get("version"));
            m.put("jarLocation", jarLocation);
            jarInfoList.add(m);
        }

        public List<String> getJarFileList() {
            return Collections.unmodifiableList(jarFileList);
        }

        public List<Map<String, String>> getJarInfoList() {
            return Collections.unmodifiableList(jarInfoList);
        }

    }

}
