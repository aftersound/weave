package io.aftersound.weave.maven;

import com.jcabi.log.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

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
        final Set<String> jarNames = new HashSet<>();
        jarInfoList.forEach(
                ji -> {
                    final String artifactId = ji.get("artifactId");
                    final String version = ji.get("version");
                    final String jarName = artifactId + "-" + version + ".jar";

                    jarNames.add(jarName);
                }
        );

        Files.walkFileTree(Paths.get(localMavenRepository.getAbsolutePath()), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (jarNames.contains(file.getFileName().toString())) {
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

                    try {
                        action.act(file.toString(), libInfo);
                    } catch (Exception e) {
                        throw new IOException(e);
                    }
                }

                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException e) throws IOException {
                Logger.error("Failed to process '{}'", file.toString(), e);
                return CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return CONTINUE;
            }
        });
    }

    public interface Action {
        void act(String file, Map<String, String> libraryInfo) throws Exception;
    }

    public static final class CompositeAction implements Action {

        private final Action[] actions;

        public CompositeAction(Action... actions) {
            this.actions = actions;
        }

        @Override
        public void act(String file, Map<String, String> libraryInfo) throws Exception {
            for (Action action : actions) {
                action.act(file, libraryInfo);
            }
        }
    }

}
