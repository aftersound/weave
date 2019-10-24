package io.aftersound.weave.service.admin;

import org.apache.ivy.Ivy;
import org.apache.ivy.core.module.descriptor.*;
import org.apache.ivy.core.module.id.ArtifactId;
import org.apache.ivy.core.module.id.ModuleRevisionId;
import org.apache.ivy.core.report.ResolveReport;
import org.apache.ivy.core.resolve.ResolveOptions;
import org.apache.ivy.core.settings.IvySettings;
import org.apache.ivy.plugins.matcher.PatternMatcher;
import org.apache.ivy.plugins.parser.xml.XmlModuleDescriptorWriter;
import org.apache.ivy.plugins.resolver.IBiblioResolver;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

class LibraryResolver {

    private final String repository;

    public LibraryResolver(String repository) {
        this.repository = repository;
    }

    Path resolve(String groupId, String artifactId, String version) throws Exception {
        if (repository.toLowerCase().startsWith("file://")) {
            return resolveInLocalRepository(groupId, artifactId, version);
        }

        if (repository.toLowerCase().startsWith("maven://")) {
            return resolveInNonsecuredMavenRepository(groupId, artifactId, version);
        }

        if (repository.toLowerCase().startsWith("mavens://")) {
            String root = "https://" + repository.substring("mavens://".length());
            return resolveInSecuredMavenRepository(groupId, artifactId, version);
        }

        return null;
    }

    private Path resolveInLocalRepository(String groupId, String artifactId, String version) {
        return Paths.get(
                repository.substring("files://".length() - 1),
                groupId.replace('.', '/'),
                artifactId,
                version,
                artifactId + "-" + version + ".jar"
        );
    }

    private Path resolveInNonsecuredMavenRepository(String groupId, String artifactId, String version) throws Exception {
        return resolveByIvy(
                repository.replace("maven://", "http://"),
                groupId,
                artifactId,
                version);
    }

    private Path resolveInSecuredMavenRepository(String groupId, String artifactId, String version) throws Exception {
        return resolveByIvy(
                repository.replace("mavens://", "https://"),
                groupId,
                artifactId,
                version);
    }

    // see https://ant.apache.org/ivy/history/master/resolver/ibiblio.html
    // see https://ant.apache.org/ivy/history/master/settings/version-matchers.html
    private static Path resolveByIvy(String repository, String groupId, String artifactId, String version) throws Exception {
        IvySettings ivySettings = new IvySettings();

        // resolver
        IBiblioResolver resolver = new IBiblioResolver();
        resolver.setUseMavenMetadata(true);
        resolver.setM2compatible(true);
        resolver.setUsepoms(true);
        resolver.setName("m2");
        resolver.setRoot(repository);
        resolver.setPattern("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier]).[ext]");
        resolver.setCheckmodified(true);
        resolver.setChangingPattern(".*SNAPSHOT");
        ivySettings.addResolver(resolver);
        ivySettings.setDefaultResolver(resolver.getName());

        //creates an Ivy instance with settings
        Ivy ivy = Ivy.newInstance(ivySettings);

        File ivyfile = File.createTempFile("ivy", ".xml");
        ivyfile.deleteOnExit();


        DefaultModuleDescriptor md = new DefaultModuleDescriptor(
                ModuleRevisionId.newInstance(
                        groupId,
                        artifactId + "-caller",
                        "working"
                ),
                "integration",
                null,
                true
        );

        DefaultDependencyDescriptor dd = new DefaultDependencyDescriptor(
                md,
                ModuleRevisionId.newInstance(
                        groupId,
                        artifactId,
                        version
                ),
                true,       // force
                false,      // changing = false,
                false       // transitive = false, no transitive dependency resolving
        );
        // exclude source.jar and javadoc.jar
        dd.addDependencyConfiguration("*", "*,!sources,!javadoc");

        md.addDependency(dd);

        //creates an ivy configuration file
        XmlModuleDescriptorWriter.write(md, ivyfile);

        ResolveOptions resolveOptions = new ResolveOptions().setConfs(
                new String[] { "default" }
        );

        // init resolve report
        ResolveReport report = ivy.resolve(ivyfile, resolveOptions);

        // so you can get the jar library
        File jarArtifactFile = report.getAllArtifactsReports()[0].getLocalFile();
        return jarArtifactFile.toPath();
    }

}
