package io.aftersound.weave.maven;

import com.jcabi.log.Logger;

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

    public Resolution resolveMavenArtifacts(List<Map<String, Object>> mavenArtifacts) {
        return aetherHelper.resolve(mavenArtifacts);
    }

    public void findAndExec(List<Map<String, Object>> jarInfoList, Action action) throws Exception {
        final Map<String, Map<String, Object>> jarInfoByJarName = new HashMap<>(jarInfoList.size());
        jarInfoList.forEach(
                ji -> {
                    final String artifactId = (String) ji.get("artifactId");
                    final String version = (String) ji.get("version");
                    final String jarName = artifactId + "-" + version + ".jar";

                    jarInfoByJarName.put(jarName, ji);
                }
        );

        Files.walkFileTree(Paths.get(localMavenRepository.getAbsolutePath()), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                final String fileName = file.getFileName().toString();
                if (jarInfoByJarName.containsKey(file.getFileName().toString())) {
                    final Map<String, Object> jarInfo = jarInfoByJarName.get(fileName);

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

                    Map<String, Object> libInfo = new LinkedHashMap<>();
                    libInfo.put("groupId", groupId);
                    libInfo.put("artifactId", artifactId);
                    libInfo.put("version", version);
                    libInfo.put("artifactFileName", artifactFileName);
                    libInfo.put("tags", jarInfo.get("tags"));

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
        void act(String file, Map<String, Object> libraryInfo) throws Exception;
    }

    public static final class CompositeAction implements Action {

        private final Action[] actions;

        public CompositeAction(Action... actions) {
            this.actions = actions;
        }

        @Override
        public void act(String file, Map<String, Object> libraryInfo) throws Exception {
            for (Action action : actions) {
                action.act(file, libraryInfo);
            }
        }
    }

}
