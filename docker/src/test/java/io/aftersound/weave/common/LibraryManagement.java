package io.aftersound.weave.common;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class LibraryManagement {

    private final LibraryManager libMgr;
    private final Path basePath;
    private final String dockerDirectory;

    public LibraryManagement(String localMavenRepository, String dockerDirectory) {
        this.libMgr = new LibraryManager(localMavenRepository);
        this.basePath = Paths.get("").toAbsolutePath();
        this.dockerDirectory = dockerDirectory;

        final String targetLibraryDirectory = basePath + "/" + dockerDirectory;
        File file = new File(targetLibraryDirectory);
        assert (file.exists() && file.isDirectory()) : (targetLibraryDirectory + " does not exist or is not directory");

        try {
            File weaveLibDir = new File(targetLibraryDirectory + "/weave-lib");
            if (weaveLibDir.exists()) {
                FileUtils.deleteDirectory(weaveLibDir);
            }

            weaveLibDir.mkdir();
            new File(targetLibraryDirectory + "/weave-lib/beam").mkdir();
            new File(targetLibraryDirectory + "/weave-lib/service").mkdir();
        } catch (IOException ioe) {
        }
    }

    public LibraryManagement(String dockerDirectory) {
        this(System.getProperty("user.home") + "/.m2/repository", dockerDirectory);
    }

    public void execute() throws Exception {
        installServiceLibraries();
        installBeamLibraries();
        generateBeamLibraryList();
    }

    private void installServiceLibraries() throws Exception {
        final LibraryManager.Action action = new LibraryManager.CopyTo(basePath + "/" + dockerDirectory + "/weave-lib/service");
        libMgr.findAndExec(basePath + "/" + dockerDirectory + "/service-service-lib.list", action);
    }

    private void installBeamLibraries() throws Exception {
        final LibraryManager.Action action = new LibraryManager.CopyTo(basePath + "/" + dockerDirectory + "/weave-lib/beam");
        libMgr.findAndExec(basePath + "/" + dockerDirectory + "/beam-service-lib.list", action);
    }

    private void generateBeamLibraryList() throws Exception {
        final ActionForDockerImage actionForDockerImage = new ActionForDockerImage();
        final ActionForServiceTest actionForServiceTest = new ActionForServiceTest();
        final LibraryManager.Action action = new LibraryManager.CompositeAction(actionForDockerImage, actionForServiceTest);

        libMgr.findAndExec(basePath + "/" + dockerDirectory + "/beam-service-lib.list", action);

        System.err.println("JAR NAME LIST:\n");
        System.err.println(toPrettyString(action.getJarNameList(), false));
        System.err.println(toPrettyString(action.getJarNameList(), true));

        System.err.println("JAR FILE LIST FOR DOCKER IMAGE:\n");
        System.err.println(toPrettyString(actionForDockerImage.getJarFileList(), true));

        System.err.println("JAR FILE LIST FOR SERVICE TEST:\n");
        System.err.println(toPrettyString(actionForServiceTest.getJarFileList(), true));
    }

    private String toPrettyString(List<?> list, boolean withDoubleQuote) {
        StringBuilder sb = new StringBuilder();
        for (Object e : list) {
            if (withDoubleQuote) {
                sb.append("\"").append(e).append("\",\n");
            } else {
                sb.append(e).append("\n");
            }
        }
        return sb.toString();
    }

    private static class ActionForDockerImage extends LibraryManager.Action {

        private final List<String> list = new ArrayList<>(50);

        @Override
        public void act(File file, Map<String, String> libraryInfo) throws Exception {
            final String groupId = libraryInfo.get("groupId");
            final String version = libraryInfo.get("version");
            final String artifactId = libraryInfo.get("artifactId");
            final String artifactFileName = libraryInfo.get("artifactFileName");

            final String targetFileName = groupId + "__" + artifactId + "__" + version + "__" + artifactFileName;
            list.add("file:/opt/weave/lib/beam/" + targetFileName);
        }

        public List<String> getJarFileList() {
            return Collections.unmodifiableList(list);
        }

    }

    private static class ActionForServiceTest extends LibraryManager.Action {

        private final List<String> list = new ArrayList<>(50);

        @Override
        public void act(File file, Map<String, String> libraryInfo) {
            list.add(file.toURI().toString());
        }

        public List<String> getJarFileList() {
            return Collections.unmodifiableList(list);
        }

    }

}
