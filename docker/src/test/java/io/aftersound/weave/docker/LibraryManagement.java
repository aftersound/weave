package io.aftersound.weave.docker;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.common.MavenLibraryHelper;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;

public class LibraryManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryManagement.class);

    private final MavenLibraryHelper mvnLibHelper;
    private final String workDir;

    public LibraryManagement(String localMavenRepository, String dockerSourceDirectory) {
        this.mvnLibHelper = new MavenLibraryHelper(localMavenRepository);
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

        try {
            File weave4devDir = new File(workDir + "/weave4dev");
            if (weave4devDir.isDirectory() && weave4devDir.exists()) {
                FileUtils.deleteDirectory(weave4devDir);
            }
            weave4devDir.mkdir();

            new File(workDir + "/weave4dev/lib").mkdir();
            new File(workDir + "/weave4dev/lib/beam").mkdir();
            new File(workDir + "/weave4dev/lib/service").mkdir();
        } catch (IOException ioe) {
        }
    }

    public LibraryManagement(String dockerDirectory) {
        this(System.getProperty("user.home") + "/.m2/repository", dockerDirectory);
    }

    public void execute() throws Exception {
        processServiceLibrariesForDocker();
        processBeamLibrariesForDocker();

        processServiceLibrariesForDev();
        processBeamLibrariesForDev();
    }

    private void processServiceLibrariesForDocker() throws Exception {
        final String sourceLibList = workDir + "/service-lib.list";
        final String libDir = workDir + "/weave/lib/service";
        final String libDirInList = "/opt/weave/lib/service";   // the path in docker container
        processLibraries(sourceLibList, libDir, libDirInList);
    }

    private void processServiceLibrariesForDev() throws Exception {
        final String sourceLibList = workDir + "/service-lib-dev.list";
        final String libDir = workDir + "/weave4dev/lib/service";
        final String libDirInList = libDir;
        processLibraries(sourceLibList, libDir, libDirInList);
    }

    private void processBeamLibrariesForDocker() throws Exception {
        final String sourceLibList = workDir + "/beam-lib.list";
        final String libDir = workDir + "/weave/lib/beam";
        final String libDirInList = "/opt/weave/lib/beam";   // the path in docker container
        processLibraries(sourceLibList, libDir, libDirInList);
    }

    private void processBeamLibrariesForDev() throws Exception {
        final String sourceLibList = workDir + "/beam-lib.list";
        final String libDir = workDir + "/weave4dev/lib/beam";
        final String libDirInList = libDir;
        processLibraries(sourceLibList, libDir, libDirInList);
    }

    private void processLibraries(
            final String sourceLibList,
            final String libDir,
            final String libDirForList) throws Exception {

        final MavenLibraryHelper.Action copyTo = new CopyTo(libDir);
        final LibraryListGenerator libraryListGenerator = new LibraryListGenerator(libDirForList);

        mvnLibHelper.findAndExec(sourceLibList, new MavenLibraryHelper.CompositeAction(copyTo, libraryListGenerator));

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

    public static class CopyTo extends MavenLibraryHelper.Action {

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

    private static class LibraryListGenerator extends MavenLibraryHelper.Action {

        private final List<String> jarFilelist = new ArrayList<>(100);
        private final List<Map<String, String>> jarInfoList = new ArrayList<>(100);

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

            Map<String, String> m = new LinkedHashMap<>(5);
            m.put("groupId", groupId);
            m.put("artifactId", artifactId);
            m.put("version", version);
            m.put("jarLocation", targetFileName);
            jarInfoList.add(m);
        }

        public List<String> getJarFileList() {
            return Collections.unmodifiableList(jarFilelist);
        }

        public List<Map<String, String>> getJarInfoList() {
            return Collections.unmodifiableList(jarInfoList);
        }

    }

    private static class ForServiceTestBeamLibraryListGenerator extends MavenLibraryHelper.Action {

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
