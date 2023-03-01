package io.aftersound.weave.service.management;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentFactory;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.component.SimpleComponentConfig;
import io.aftersound.weave.hikari3x.HikariDataSourceFactory;
import io.aftersound.weave.hikari3x.HikariDatabaseInitializerFactory;
import io.aftersound.weave.hsqldb.HSQLDBFactory;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ManagementTest {

    private static ComponentRegistry componentRegistry;

    @BeforeClass
    public static void setup() throws Exception {
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> bindings = ActorBindingsUtil.loadActorBindings(
                Arrays.asList(
                        HSQLDBFactory.class.getName(),
                        HikariDataSourceFactory.class.getName(),
                        HikariDatabaseInitializerFactory.class.getName()
                ),
                ComponentConfig.class,
                Object.class,
                false
        );

        componentRegistry = new ComponentRegistry(bindings);
        componentRegistry.initializeComponent(
                SimpleComponentConfig.of(
                        "HSQLDB",
                        "weavedb",
                        MapBuilder.hashMap()
                                .kv("server.database.0", "mem:weavetest")
                                .kv("server.dbname.0", "weavetest")
                                .build()
                )
        );
        componentRegistry.initializeComponent(
                SimpleComponentConfig.of(
                        "Hikari3xDatabaseInitializer",
                        "hikari3x.database.initializer",
                        MapBuilder.hashMap()
                                .kv("jdbc.url", "jdbc:hsqldb:mem:weavetest")
                                .kv("driver.class.name", "org.hsqldb.jdbc.JDBCDriver")
                                .kv("username", "sa")
                                .kv("password", "")
                                .kv("init.script", "Q1JFQVRFIFRBQkxFIElGIE5PVCBFWElTVFMgbmFtZXNwYWNlCigKICAgIG5hbWUgVkFSQ0hBUigyNTUpIE5PVCBOVUxMLAogICAgb3duZXIgVkFSQ0hBUigyNTUpLAogICAgb3duZXJfZW1haWwgVkFSQ0hBUigyNTUpLAogICAgZGVzY3JpcHRpb24gVkFSQ0hBUig0MDk2KSwKICAgIGF0dHJpYnV0ZXMgVkFSQ0hBUig0MDk2KSwKICAgIGNyZWF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgdXBkYXRlZCBUSU1FU1RBTVAoMykgTk9UIE5VTEwsCiAgICB0cmFjZSBWQVJDSEFSKDI1NSksCiAgICBQUklNQVJZIEtFWSAobmFtZSkKKTsKQ1JFQVRFIElOREVYIElGIE5PVCBFWElTVFMgaWR4X25zX293bmVyIE9OIG5hbWVzcGFjZSAob3duZXIpOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpbnhfbnNfb3duZXJfZW1haWwgT04gbmFtZXNwYWNlIChvd25lcl9lbWFpbCk7CgpDUkVBVEUgVEFCTEUgSUYgTk9UIEVYSVNUUyBuYW1lc3BhY2VfaGlzdG9yeQooCiAgICBpZCBJTlRFR0VSIElERU5USVRZIFBSSU1BUlkgS0VZLAogICAgbmFtZSBWQVJDSEFSKDI1NSkgTk9UIE5VTEwsCiAgICBvd25lciBWQVJDSEFSKDI1NSksCiAgICBvd25lcl9lbWFpbCBWQVJDSEFSKDI1NSksCiAgICBkZXNjcmlwdGlvbiBWQVJDSEFSKDQwOTYpLAogICAgYXR0cmlidXRlcyBWQVJDSEFSKDQwOTYpLAogICAgY3JlYXRlZCBUSU1FU1RBTVAoMykgTk9UIE5VTEwsCiAgICB1cGRhdGVkIFRJTUVTVEFNUCgzKSBOT1QgTlVMTCwKICAgIHRyYWNlIFZBUkNIQVIoMjU1KQopOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfbnNoX25hbWUgT04gbmFtZXNwYWNlX2hpc3RvcnkgKG5hbWUpOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfbnNoX293bmVyIE9OIG5hbWVzcGFjZV9oaXN0b3J5IChvd25lcik7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlueF9uc2hfb3duZXJfZW1haWwgT04gbmFtZXNwYWNlX2hpc3RvcnkgKG93bmVyX2VtYWlsKTsKCkNSRUFURSBUQUJMRSBJRiBOT1QgRVhJU1RTIGFwcGxpY2F0aW9uCigKICAgIG5hbWVzcGFjZSBWQVJDSEFSKDI1NSkgTk9UIE5VTEwsCiAgICBuYW1lIFZBUkNIQVIoMjU1KSBOT1QgTlVMTCwKICAgIG93bmVyIFZBUkNIQVIoMjU1KSwKICAgIG93bmVyX2VtYWlsIFZBUkNIQVIoMjU1KSwKICAgIGRlc2NyaXB0aW9uIFZBUkNIQVIoNDA5NiksCiAgICBhdHRyaWJ1dGVzIFZBUkNIQVIoNDA5NiksCiAgICBjcmVhdGVkIFRJTUVTVEFNUCgzKSBOT1QgTlVMTCwKICAgIHVwZGF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgdHJhY2UgVkFSQ0hBUigyNTUpLAogICAgUFJJTUFSWSBLRVkgKG5hbWVzcGFjZSxuYW1lKQopOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfYXBwX293bmVyIE9OIGFwcGxpY2F0aW9uIChvd25lcik7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlueF9hcHBfb3duZXJfZW1haWwgT04gYXBwbGljYXRpb24gKG93bmVyX2VtYWlsKTsKCkNSRUFURSBUQUJMRSBJRiBOT1QgRVhJU1RTIGFwcGxpY2F0aW9uX2hpc3RvcnkKKAogICAgaWQgSU5URUdFUiBJREVOVElUWSBQUklNQVJZIEtFWSwKICAgIG5hbWVzcGFjZSBWQVJDSEFSKDI1NSkgTk9UIE5VTEwsCiAgICBuYW1lIFZBUkNIQVIoMjU1KSBOT1QgTlVMTCwKICAgIG93bmVyIFZBUkNIQVIoMjU1KSwKICAgIG93bmVyX2VtYWlsIFZBUkNIQVIoMjU1KSwKICAgIGRlc2NyaXB0aW9uIFZBUkNIQVIoNDA5NiksCiAgICBhdHRyaWJ1dGVzIFZBUkNIQVIoNDA5NiksCiAgICBjcmVhdGVkIFRJTUVTVEFNUCgzKSBOT1QgTlVMTCwKICAgIHVwZGF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgdHJhY2UgVkFSQ0hBUigyNTUpLAopOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfYWhfbmFtZXNwYWNlIE9OIGFwcGxpY2F0aW9uX2hpc3RvcnkgKG5hbWVzcGFjZSk7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlkeF9haF9uYW1lIE9OIGFwcGxpY2F0aW9uX2hpc3RvcnkgKG5hbWUpOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfYWhfb3duZXIgT04gYXBwbGljYXRpb25faGlzdG9yeSAob3duZXIpOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpbnhfYWhfb3duZXJfZW1haWwgT04gYXBwbGljYXRpb25faGlzdG9yeSAob3duZXJfZW1haWwpOwoKQ1JFQVRFIFRBQkxFIElGIE5PVCBFWElTVFMgcnVudGltZV9jb25maWcKKAogICAgayBWQVJDSEFSKDUxMikgUFJJTUFSWSBLRVksCiAgICB2IExPTkdWQVJCSU5BUlksCiAgICBjcmVhdGVkIFRJTUVTVEFNUCgzKSBOT1QgTlVMTCwKICAgIHVwZGF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgdHJhY2UgVkFSQ0hBUigyNTUpCik7CgpDUkVBVEUgVEFCTEUgSUYgTk9UIEVYSVNUUyBydW50aW1lX2NvbmZpZ19oaXN0b3J5CigKICAgIGlkIElOVEVHRVIgSURFTlRJVFkgUFJJTUFSWSBLRVksCiAgICBrIFZBUkNIQVIoNTEyKSBOT1QgTlVMTCwKICAgIHYgTE9OR1ZBUkJJTkFSWSwKICAgIGNyZWF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgdXBkYXRlZCBUSU1FU1RBTVAoMykgTk9UIE5VTEwsCiAgICB0cmFjZSBWQVJDSEFSKDI1NSkKKTsKCkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlkeF9rIE9OIHJ1bnRpbWVfY29uZmlnX2hpc3RvcnkgKGspOwoKQ1JFQVRFIFRBQkxFIElGIE5PVCBFWElTVFMgaW5zdGFuY2UKKAogICAgaG9zdCBWQVJDSEFSKDI1NSkgTk9UIE5VTEwsCiAgICBwb3J0IElOVEVHRVIgTk9UIE5VTEwsCiAgICBpcHY0X2FkZHJlc3MgVkFSQ0hBUigyNTUpLAogICAgaXB2Nl9hZGRyZXNzIFZBUkNIQVIoMjU1KSwKICAgIG5hbWVzcGFjZSBWQVJDSEFSKDI1NSkgTk9UIE5VTEwsCiAgICBhcHBsaWNhdGlvbiBWQVJDSEFSKDI1NSkgTk9UIE5VTEwsCiAgICBlbnZpcm9ubWVudCBWQVJDSEFSKDI1NSksCiAgICBzdGF0dXMgVkFSQ0hBUigzMSkgTk9UIE5VTEwsCiAgICB1cGRhdGVkIFRJTUVTVEFNUCgzKSBOT1QgTlVMTCwKICAgIFBSSU1BUlkgS0VZIChob3N0LHBvcnQpLAopOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfYWlfbmFtZXNwYWNlIE9OIGluc3RhbmNlIChuYW1lc3BhY2UpOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfYWlfYXBwbGljYXRpb24gT04gaW5zdGFuY2UgKGFwcGxpY2F0aW9uKTs=")
                                .build()
                )
        );
        componentRegistry.initializeComponent(
                SimpleComponentConfig.of(
                        "Hikari3xDataSource",
                        "src.data.source",
                        MapBuilder.hashMap()
                                .kv("jdbc.url", "jdbc:hsqldb:mem:weavetest")
                                .kv("driver.class.name", "org.hsqldb.jdbc.JDBCDriver")
                                .kv("username", "sa")
                                .kv("password", "")
                                .build()
                )

        );
    }

    @Test
    public void test() {
        DataSource dataSource = componentRegistry.getComponent("src.data.source");
        NamespaceManager namespaceManager = new NamespaceManager(dataSource, "system");
        ApplicationManager applicationManager = new ApplicationManager(dataSource, "system");
        InstanceManager instanceManager = new InstanceManager(dataSource, "system");

        Namespace ns = new Namespace();
        ns.setName("test");
        ns.setOwner("test");
        ns.setOwnerEmail("test@test.com");
        ns.setDescription("test namespace");
        ns.setAttributes(new HashMap<>());
        namespaceManager.createNamespace(ns);

        Application app = new Application();
        app.setNamespace("test");
        app.setName("test");
        app.setOwner("test");
        app.setOwnerEmail("test@test.com");
        app.setAttributes(new HashMap<>());
        app.setDescription("test application");
        applicationManager.createApplication(app);

        Instance i1 = new Instance();
        i1.setHost("localhost");
        i1.setPort(8888);
        i1.setIpv4Address("127.0.0.1");
        i1.setNamespace("test");
        i1.setApplication("test");
        i1.setEnvironment("QA");
        instanceManager.registerInstance(i1);
        Instance i2 = new Instance();
        i2.setHost("localhost");
        i2.setPort(9999);
        i2.setIpv4Address("127.0.0.1");
        i2.setNamespace("test");
        i2.setApplication("test");
        i2.setEnvironment("QA");
        instanceManager.registerInstance(i2);

        instanceManager.markup(i1);
        instanceManager.markup(i2);
        List<Instance> instances = instanceManager.findInstances(
                MapBuilder.linkedHashMap()
                        .kv("namespace", "test")
                        .kv("application", "test")
                        .kv("status", "up")
                        .build(),
                100,
                0
        );
        assertEquals(2, instances.size());

        instanceManager.markdown(i1);
        instances = instanceManager.findInstances(
                MapBuilder.linkedHashMap()
                        .kv("namespace", "test")
                        .kv("application", "test")
                        .kv("status", "up")
                        .build(),
                100,
                0
        );
        assertEquals(1, instances.size());

        instanceManager.markdown(i2);
        instances = instanceManager.findInstances(
                MapBuilder.linkedHashMap()
                        .kv("namespace", "test")
                        .kv("application", "test")
                        .kv("status", "up")
                        .build(),
                100,
                0
        );
        assertEquals(0, instances.size());

        instances = instanceManager.findInstances(
                MapBuilder.linkedHashMap()
                        .kv("namespace", "test")
                        .kv("application", "test")
                        .build(),
                100,
                0
        );
        assertEquals(2, instances.size());

        instanceManager.unregisterInstance(i1);
        instanceManager.unregisterInstance(i2);

        instances = instanceManager.findInstances(
                MapBuilder.linkedHashMap()
                        .kv("namespace", "test")
                        .kv("application", "test")
                        .build(),
                100,
                0
        );
        assertEquals(0, instances.size());
    }

}