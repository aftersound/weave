package io.aftersound.weave.maven;

import com.jcabi.aether.Aether;
import io.aftersound.weave.utils.MapBuilder;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.util.*;

public class AetherHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(AetherHelper.class);

    private final Aether aether;

    public AetherHelper(final List<ArtifactRepository> remoteRepositories, final ArtifactRepository localRepository) {
        final List<RemoteRepository> remoteRepositoryList = new LinkedList<>();
        remoteRepositories.forEach(r -> remoteRepositoryList.add(new RemoteRepository(r.getId(), r.getType(), r.getUrl())));

        File localRepositoryDir = new File(localRepository.getUrl());

        this.aether = new Aether(remoteRepositoryList, localRepositoryDir);
    }

    public Resolution resolve(List<Map<String, Object>> targetArtifactList) {
        final List<Map<String, String>> resolved = new LinkedList<>();
        final List<Map<String, String>> unresolved = new LinkedList<>();

        List<Artifact> resolvedArtifacts = new LinkedList<>();
        targetArtifactList.forEach(
                m -> {
                    try {
                        final String groupId = (String) m.get("groupId");
                        final String artifactId = (String) m.get("artifactId");
                        final String version = (String) m.get("version");
                        List<Artifact> artifacts = aether.resolve(
                                new DefaultArtifact(groupId, artifactId, "jar", version),
                                JavaScopes.COMPILE
                        );
                        resolved.add(
                                MapBuilder.linkedHashMap()
                                        .keys("groupId", "artifactId", "version")
                                        .values(m.get("groupId"), m.get("artifactId"), m.get("version"))
                                        .build()
                        );

                        resolvedArtifacts.addAll(artifacts);
                    } catch (DependencyResolutionException e) {
                        LOGGER.error("Failed to resolve artifact '{}", m, e);
                        unresolved.add(
                                MapBuilder.linkedHashMap()
                                        .keys("groupId", "artifactId", "version")
                                        .values(m.get("groupId"), m.get("artifactId"), m.get("version"))
                                        .build()
                        );
                    }
                }
        );

        List<Map<String, String>> requstedWithDependencies = new LinkedList<>();
        resolvedArtifacts.forEach(
                artifact -> {
                    if ("jar".equals(artifact.getExtension())) {
                        requstedWithDependencies.add(
                                MapBuilder.linkedHashMap()
                                        .keys("groupId", "artifactId", "version", "jarLocation")
                                        .values(
                                                artifact.getGroupId(),
                                                artifact.getArtifactId(),
                                                artifact.getVersion(),
                                                artifact.getFile().toString()
                                        )
                                        .build()
                        );
                    }
                }
        );

        return new Resolution(resolved, unresolved, requstedWithDependencies);
    }

}
