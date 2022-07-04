package io.aftersound.weave.maven;

import com.jcabi.aether.Aether;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.junit.Test;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.util.artifact.DefaultArtifact;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class MavenHelperTest {

    @Test
    public void test() throws Exception {
        Aether aether = new Aether(
                Arrays.asList(
                        new RemoteRepository(
                                "maven-central",
                                "default",
                                "https://repo1.maven.org/maven2/"
                        )
                ),
                new File(System.getProperty("user.dir") + "/mvn")
        );
        List<Artifact> artifacts = aether.resolve(
                new DefaultArtifact("com.fasterxml.jackson.core", "jackson-core", "jar", "2.13.2"),
                JavaScopes.COMPILE
        );

        assertTrue(artifacts != null && !artifacts.isEmpty());
    }
    
//    public void test1() throws Exception {
//
//        File localRepository = new File(System.getProperty("user.dir") + "/.m2/repository");
//
//        RepositorySystem mps = MavenRepositorySystemUtils.newServiceLocator().getService(RepositorySystem.class);
////        RepositorySystem mps = MavenUtil.getRepositorySystem();
//
//        Artifact artifact = mps.createArtifact(
//                "com.fasterxml.jackson.core",
//                "jackson-core",
//                "2.13.2",
//                "jar"
//        );
//
//        List<ArtifactRepository> remoteRepositories = new ArrayList<>();
//        remoteRepositories.add(mps.createDefaultRemoteRepository());
//
//        ArtifactResolutionRequest request = new ArtifactResolutionRequest();
//        request.setArtifact(artifact);
//        request.setLocalRepository(mps.createLocalRepository(localRepository));
//        request.setOffline(false);
//        request.setRemoteRepositories(remoteRepositories);
//        request.setResolveTransitively(false);
//
//        ArtifactResolutionResult resolution = mps.resolve(request);
//
//        assertTrue(resolution.isSuccess());
//    }

//    public static RepositorySystem newRepositorySystem() {
//        DefaultRepositorySystem repositorySystem = new DefaultRepositorySystem();
//        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
//
//        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
//        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
//        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
//        locator.setService(LoggerFactory.class, NullLoggerFactory.class);
//        locator.setService(VersionResolver.class, DefaultVersionResolver.class);
//        locator.setService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);
//        locator.setService(ArtifactResolver.class, DefaultArtifactResolver.class);
//        locator.setService(MetadataResolver.class, DefaultMetadataResolver.class);
//        locator.setService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
//        locator.setService(DependencyCollector.class, DefaultDependencyCollector.class);
//        locator.setService(Installer.class, DefaultInstaller.class);
//        locator.setService(Deployer.class, DefaultDeployer.class);
//        locator.setService(LocalRepositoryProvider.class, DefaultLocalRepositoryProvider.class);
//        locator.setService(SyncContextFactory.class, DefaultSyncContextFactory.class);
//
//        repositorySystem.initService(locator);
//
//        return repositorySystem.
//    }

}
