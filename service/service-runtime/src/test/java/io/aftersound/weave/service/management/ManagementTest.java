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
import io.aftersound.util.MapBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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
                        MapBuilder.<String, String>hashMap()
                                .put("server.database.0", "mem:weavetest")
                                .put("server.dbname.0", "weavetest")
                                .build()
                )
        );
        componentRegistry.initializeComponent(
                SimpleComponentConfig.of(
                        "Hikari3xDatabaseInitializer",
                        "hikari3x.database.initializer",
                        MapBuilder.<String, String>hashMap()
                                .put("jdbc.url", "jdbc:hsqldb:mem:weavetest;sql.syntax_mys=true")
                                .put("driver.class.name", "org.hsqldb.jdbc.JDBCDriver")
                                .put("username", "sa")
                                .put("password", "")
                                .put("init.script", "U0VUIERBVEFCQVNFIFNRTCBTWU5UQVggTVlTIFRSVUU7CgpDUkVBVEUgVEFCTEUgSUYgTk9UIEVYSVNUUyBuYW1lc3BhY2UKKAogICAgbmFtZSBWQVJDSEFSKDI1NSkgTk9UIE5VTEwsCiAgICBvd25lciBWQVJDSEFSKDI1NSksCiAgICBvd25lcl9lbWFpbCBWQVJDSEFSKDI1NSksCiAgICBkZXNjcmlwdGlvbiBWQVJDSEFSKDQwOTYpLAogICAgYXR0cmlidXRlcyBWQVJDSEFSKDQwOTYpLAogICAgY3JlYXRlZCBUSU1FU1RBTVAoMykgTk9UIE5VTEwsCiAgICB1cGRhdGVkIFRJTUVTVEFNUCgzKSBOT1QgTlVMTCwKICAgIHRyYWNlIFZBUkNIQVIoMjU1KSwKICAgIFBSSU1BUlkgS0VZIChuYW1lKQopOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfbnNfb3duZXIgT04gbmFtZXNwYWNlIChvd25lcik7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlueF9uc19vd25lcl9lbWFpbCBPTiBuYW1lc3BhY2UgKG93bmVyX2VtYWlsKTsKCkNSRUFURSBUQUJMRSBJRiBOT1QgRVhJU1RTIG5hbWVzcGFjZV9oaXN0b3J5CigKICAgIGlkIElOVEVHRVIgSURFTlRJVFkgUFJJTUFSWSBLRVksCiAgICBuYW1lIFZBUkNIQVIoMjU1KSBOT1QgTlVMTCwKICAgIG93bmVyIFZBUkNIQVIoMjU1KSwKICAgIG93bmVyX2VtYWlsIFZBUkNIQVIoMjU1KSwKICAgIGRlc2NyaXB0aW9uIFZBUkNIQVIoNDA5NiksCiAgICBhdHRyaWJ1dGVzIFZBUkNIQVIoNDA5NiksCiAgICBjcmVhdGVkIFRJTUVTVEFNUCgzKSBOT1QgTlVMTCwKICAgIHVwZGF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgdHJhY2UgVkFSQ0hBUigyNTUpCik7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlkeF9uc2hfbmFtZSBPTiBuYW1lc3BhY2VfaGlzdG9yeSAobmFtZSk7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlkeF9uc2hfb3duZXIgT04gbmFtZXNwYWNlX2hpc3RvcnkgKG93bmVyKTsKQ1JFQVRFIElOREVYIElGIE5PVCBFWElTVFMgaW54X25zaF9vd25lcl9lbWFpbCBPTiBuYW1lc3BhY2VfaGlzdG9yeSAob3duZXJfZW1haWwpOwoKQ1JFQVRFIFRBQkxFIElGIE5PVCBFWElTVFMgYXBwbGljYXRpb24KKAogICAgbmFtZXNwYWNlIFZBUkNIQVIoMjU1KSBOT1QgTlVMTCwKICAgIG5hbWUgVkFSQ0hBUigyNTUpIE5PVCBOVUxMLAogICAgb3duZXIgVkFSQ0hBUigyNTUpLAogICAgb3duZXJfZW1haWwgVkFSQ0hBUigyNTUpLAogICAgZGVzY3JpcHRpb24gVkFSQ0hBUig0MDk2KSwKICAgIGF0dHJpYnV0ZXMgVkFSQ0hBUig0MDk2KSwKICAgIGNyZWF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgdXBkYXRlZCBUSU1FU1RBTVAoMykgTk9UIE5VTEwsCiAgICB0cmFjZSBWQVJDSEFSKDI1NSksCiAgICBQUklNQVJZIEtFWSAobmFtZXNwYWNlLG5hbWUpCik7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlkeF9hcHBfb3duZXIgT04gYXBwbGljYXRpb24gKG93bmVyKTsKQ1JFQVRFIElOREVYIElGIE5PVCBFWElTVFMgaW54X2FwcF9vd25lcl9lbWFpbCBPTiBhcHBsaWNhdGlvbiAob3duZXJfZW1haWwpOwoKQ1JFQVRFIFRBQkxFIElGIE5PVCBFWElTVFMgYXBwbGljYXRpb25faGlzdG9yeQooCiAgICBpZCBJTlRFR0VSIElERU5USVRZIFBSSU1BUlkgS0VZLAogICAgbmFtZXNwYWNlIFZBUkNIQVIoMjU1KSBOT1QgTlVMTCwKICAgIG5hbWUgVkFSQ0hBUigyNTUpIE5PVCBOVUxMLAogICAgb3duZXIgVkFSQ0hBUigyNTUpLAogICAgb3duZXJfZW1haWwgVkFSQ0hBUigyNTUpLAogICAgZGVzY3JpcHRpb24gVkFSQ0hBUig0MDk2KSwKICAgIGF0dHJpYnV0ZXMgVkFSQ0hBUig0MDk2KSwKICAgIGNyZWF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgdXBkYXRlZCBUSU1FU1RBTVAoMykgTk9UIE5VTEwsCiAgICB0cmFjZSBWQVJDSEFSKDI1NSksCik7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlkeF9haF9uYW1lc3BhY2UgT04gYXBwbGljYXRpb25faGlzdG9yeSAobmFtZXNwYWNlKTsKQ1JFQVRFIElOREVYIElGIE5PVCBFWElTVFMgaWR4X2FoX25hbWUgT04gYXBwbGljYXRpb25faGlzdG9yeSAobmFtZSk7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlkeF9haF9vd25lciBPTiBhcHBsaWNhdGlvbl9oaXN0b3J5IChvd25lcik7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlueF9haF9vd25lcl9lbWFpbCBPTiBhcHBsaWNhdGlvbl9oaXN0b3J5IChvd25lcl9lbWFpbCk7CgpDUkVBVEUgVEFCTEUgSUYgTk9UIEVYSVNUUyBydW50aW1lX2NvbmZpZwooCiAgICBrIFZBUkNIQVIoNTEyKSBQUklNQVJZIEtFWSwKICAgIHYgTE9OR1ZBUkJJTkFSWSwKICAgIGNyZWF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgdXBkYXRlZCBUSU1FU1RBTVAoMykgTk9UIE5VTEwsCiAgICB0cmFjZSBWQVJDSEFSKDI1NSkKKTsKCkNSRUFURSBUQUJMRSBJRiBOT1QgRVhJU1RTIHJ1bnRpbWVfY29uZmlnX2hpc3RvcnkKKAogICAgaWQgSU5URUdFUiBJREVOVElUWSBQUklNQVJZIEtFWSwKICAgIGsgVkFSQ0hBUig1MTIpIE5PVCBOVUxMLAogICAgdiBMT05HVkFSQklOQVJZLAogICAgY3JlYXRlZCBUSU1FU1RBTVAoMykgTk9UIE5VTEwsCiAgICB1cGRhdGVkIFRJTUVTVEFNUCgzKSBOT1QgTlVMTCwKICAgIHRyYWNlIFZBUkNIQVIoMjU1KQopOwoKQ1JFQVRFIElOREVYIElGIE5PVCBFWElTVFMgaWR4X2sgT04gcnVudGltZV9jb25maWdfaGlzdG9yeSAoayk7CgpDUkVBVEUgVEFCTEUgSUYgTk9UIEVYSVNUUyBpbnN0YW5jZQooCiAgICBpaWQgVkFSQ0hBUigxMjcpIE5PVCBOVUxMLAogICAgbmFtZXNwYWNlIFZBUkNIQVIoMjU1KSBOT1QgTlVMTCwKICAgIGFwcGxpY2F0aW9uIFZBUkNIQVIoMjU1KSBOT1QgTlVMTCwKICAgIGVudmlyb25tZW50IFZBUkNIQVIoMjU1KSwKICAgIGhvc3QgVkFSQ0hBUigyNTUpIE5PVCBOVUxMLAogICAgcG9ydCBJTlRFR0VSIE5PVCBOVUxMLAogICAgaXB2NF9hZGRyZXNzIFZBUkNIQVIoMjU1KSwKICAgIGlwdjZfYWRkcmVzcyBWQVJDSEFSKDI1NSksCiAgICBzdGF0dXMgVkFSQ0hBUigzMSkgTk9UIE5VTEwsCiAgICB1cGRhdGVkIFRJTUVTVEFNUCgzKSBOT1QgTlVMTCwKICAgIFBSSU1BUlkgS0VZIChpaWQpLAopOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfYWlfbmFtZXNwYWNlIE9OIGluc3RhbmNlIChuYW1lc3BhY2UpOwpDUkVBVEUgSU5ERVggSUYgTk9UIEVYSVNUUyBpZHhfYWlfYXBwbGljYXRpb24gT04gaW5zdGFuY2UgKGFwcGxpY2F0aW9uKTsKQ1JFQVRFIElOREVYIElGIE5PVCBFWElTVFMgaWR4X2FpX2Vudmlyb25tZW50IE9OIGluc3RhbmNlIChlbnZpcm9ubWVudCk7CkNSRUFURSBJTkRFWCBJRiBOT1QgRVhJU1RTIGlkeF9haV9ob3N0IE9OIGluc3RhbmNlIChob3N0KTsKQ1JFQVRFIElOREVYIElGIE5PVCBFWElTVFMgaWR4X2FpX3N0YXR1cyBPTiBpbnN0YW5jZSAoc3RhdHVzKTs=")
                                .build()
                )
        );
        componentRegistry.initializeComponent(
                SimpleComponentConfig.of(
                        "Hikari3xDataSource",
                        "src.data.source",
                        MapBuilder.<String, String>hashMap()
                                .put("jdbc.url", "jdbc:hsqldb:mem:weavetest;sql.syntax_mys=true")
                                .put("driver.class.name", "org.hsqldb.jdbc.JDBCDriver")
                                .put("username", "sa")
                                .put("password", "")
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
        i1.setId(UUID.randomUUID().toString());
        i1.setHost("localhost");
        i1.setPort(8888);
        i1.setIpv4Address("127.0.0.1");
        i1.setNamespace("test");
        i1.setApplication("test");
        i1.setEnvironment("QA");
        instanceManager.registerInstance(i1);
        Instance i2 = new Instance();
        i2.setId(UUID.randomUUID().toString());
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
                MapBuilder.<String, String>linkedHashMap()
                        .put("namespace", "test")
                        .put("application", "test")
                        .put("status", "up")
                        .build(),
                100,
                0
        );
        assertEquals(2, instances.size());

        instanceManager.markdown(i1);
        instances = instanceManager.findInstances(
                MapBuilder.<String, String>linkedHashMap()
                        .put("namespace", "test")
                        .put("application", "test")
                        .put("status", "up")
                        .build(),
                100,
                0
        );
        assertEquals(1, instances.size());

        instanceManager.markdown(i2);
        instances = instanceManager.findInstances(
                MapBuilder.<String, String>linkedHashMap()
                        .put("namespace", "test")
                        .put("application", "test")
                        .put("status", "up")
                        .build(),
                100,
                0
        );
        assertEquals(0, instances.size());

        instances = instanceManager.findInstances(
                MapBuilder.<String, String>linkedHashMap()
                        .put("namespace", "test")
                        .put("application", "test")
                        .build(),
                100,
                0
        );
        assertEquals(2, instances.size());

        instanceManager.unregisterInstance(i1);
        instanceManager.unregisterInstance(i2);

        instances = instanceManager.findInstances(
                MapBuilder.<String, String>linkedHashMap()
                        .put("namespace", "test")
                        .put("application", "test")
                        .build(),
                100,
                0
        );
        assertEquals(0, instances.size());
    }

}