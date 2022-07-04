package io.aftersound.weave.maven;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.file.FileVisitResult.CONTINUE;

public class MavenHelper {

    private final AetherHelper aetherHelper;
    private final File localMavenRepository;
    private final int baseIndex;

    public MavenHelper(List<ArtifactRepository> remoteRepositories, ArtifactRepository localRepository) {
        this.aetherHelper = new AetherHelper(remoteRepositories, localRepository);

        final String localMavenRepository = localRepository.getUrl();
        File localMavenRepo = new File(localMavenRepository);
        assert localMavenRepo.exists() && localMavenRepo.isDirectory() : (localMavenRepository + " is not a directory");
        this.localMavenRepository = localMavenRepo;
        this.baseIndex = localMavenRepository.split("\\/").length;
    }

    public MavenHelper(String localMavenRepository) {
        this(
                Arrays.asList(
                        new ArtifactRepository(
                                "maven-central",
                                "default",
                                "https://repo1.maven.org/maven2/"
                        )
                ),
                new ArtifactRepository(
                        "local",
                        "local",
                        localMavenRepository
                )
        );
    }

    public MavenHelper() {
        this(
                Arrays.asList(
                        new ArtifactRepository(
                                "maven-central",
                                "default",
                                "https://repo1.maven.org/maven2/"
                        )
                ),
                new ArtifactRepository(
                        "local",
                        "local",
                        System.getProperty("user.home") + "/.m2/repository"
                )
        );
    }

    public Resolution resolveMavenArtifacts(List<Map<String, String>> mavenArtifacts) {
        return aetherHelper.resolve(mavenArtifacts);
    }

    public void findAndExec(List<Map<String, String>> jarInfoList, Action action) throws Exception {
        final List<String> jarNames = new LinkedList<>();
        jarInfoList.forEach(
                ji -> {
                    final String artifactId = ji.get("artifactId");
                    final String version = ji.get("version");
                    final String jarName = artifactId + "-" + version + ".jar";

                    jarNames.add(jarName);
                }
        );

        Collections.sort(jarNames, Comparator.naturalOrder());

        action.setJarNameList(Collections.unmodifiableList(jarNames));

        Collection<File> files = FileUtils.listFiles(
                localMavenRepository,
                new NameFileFilter(jarNames.toArray(new String[jarNames.size()])),
                DirectoryFileFilter.INSTANCE
        );

        Collections.sort((List<File>) files, Comparator.comparing(File::toString));

        for (File file : files) {
            String[] strArray = file.toString().split("\\/");
            final int length = strArray.length;

            StringJoiner groupIdBuilder = new StringJoiner(".");
            for (int i = baseIndex; i <= length - 4; i++) {
                groupIdBuilder.add(strArray[i]);
            }
            final String groupId = groupIdBuilder.toString();
            final String version = strArray[length - 2];
            final String artifactId = strArray[length - 3];
            final String artifactFileName = strArray[length - 1];

            Map<String, String> libInfo = new HashMap<>(4);
            libInfo.put("groupId", groupId);
            libInfo.put("artifactId", artifactId);
            libInfo.put("version", version);
            libInfo.put("artifactFileName", artifactFileName);

            action.act(file, libInfo);
        }
    }

    public void findAndExec1(List<Map<String, String>> jarInfoList, Action action) throws Exception {
        Files.walkFileTree(Paths.get(localMavenRepository.getAbsolutePath()), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                System.out.println(dir.toString());
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Files.isRegularFile(file) && file.getFileName().toString().toLowerCase().endsWith(".jar")) {
                    System.out.println(file);
                    ZipInputStream zis = new ZipInputStream(new FileInputStream(file.toFile()));
                    ZipEntry ze;
                    while ((ze = zis.getNextEntry()) != null) {
                        if (ze.toString().endsWith("pom.xml")) {
                            System.out.println("\t\t\t" + ze);
                        }
                    }
//                    ZipFile zipFile = null;
//                    try {
//                        zipFile = new ZipFile(file.toFile());
//                        zipFile.ge
//                        ZipEntry zipEntry = zipFile.getEntry("META-INF/weave/extensions.json");
//                        zipEntry.
//                        ExtensionInfo[] extensionInfos = Helper.JSON_MAPPER.readValue(
//                                zipFile.getInputStream(zipEntry),
//                                ExtensionInfo[].class
//                        );
//                        return Arrays.asList(extensionInfos);
//                    } catch (Exception e) {
//                        return null;
//                    } finally {
//                        if (zipFile != null) {
//                            try {
//                                zipFile.close();
//                            } catch (Exception e) {
//                            }
//                        }
//                    }
                } else {
//                    System.out.println("\t\t" + file);
                }
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                System.out.println(file.toString());
                return CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                System.out.println(dir.toString());
                return CONTINUE;
            }
        });
    }

    public static abstract class Action {

        private List<String> jarNameList;

        protected void setJarNameList(List<String> jarNameList) {
            this.jarNameList = jarNameList;
        }

        public List<String> getJarNameList() {
            return jarNameList;
        }

        public abstract void act(File file, Map<String, String> libraryInfo) throws Exception;
    }

    public static final class CompositeAction extends Action {

        private final Action[] actions;

        public CompositeAction(Action... actions) {
            this.actions = actions;
        }

        @Override
        protected void setJarNameList(List<String> jarNameList) {
            super.setJarNameList(jarNameList);
            for (Action action : actions) {
                action.setJarNameList(jarNameList);
            }
        }

        @Override
        public void act(File file, Map<String, String> libraryInfo) throws Exception {
            for (Action action : actions) {
                action.act(file, libraryInfo);
            }
        }
    }

}
