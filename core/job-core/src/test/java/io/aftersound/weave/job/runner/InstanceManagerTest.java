package io.aftersound.weave.job.runner;

import io.aftersound.weave.actor.ActorBindings;
import io.aftersound.weave.actor.ActorBindingsUtil;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentFactory;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.component.SimpleComponentConfig;
import io.aftersound.weave.hikari3x.HikariDataSourceFactory;
import io.aftersound.weave.hikari3x.HikariDatabaseInitializerFactory;
import io.aftersound.weave.utils.MapBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class InstanceManagerTest {

    private static ComponentRegistry componentRegistry;

    @BeforeClass
    public static void setup() throws Exception {
        ActorBindings<ComponentConfig, ComponentFactory<?>, Object> bindings = ActorBindingsUtil.loadActorBindings(
                Arrays.asList(
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
                        "Hikari3xDatabaseInitializer",
                        "src.data.source.initializer",
                        MapBuilder.linkedHashMap()
                                .kv("driver.class.name", "com.mysql.cj.jdbc.Driver")
                                .kv("jdbc.url", "jdbc:mysql://localhost:3306/test")
                                .kv("username", "root")
                                .kv("password", "password")
                                .kv("init.script", "RFJPUCBUQUJMRSBydW5uZXJfaW5zdGFuY2U7CgpDUkVBVEUgVEFCTEUgSUYgTk9UIEVYSVNUUyBydW5uZXJfaW5zdGFuY2UKKAogICAgaWlkIFZBUkNIQVIoMTI3KSBOT1QgTlVMTCwKICAgIG5hbWVzcGFjZSBWQVJDSEFSKDI1NSkgTk9UIE5VTEwsCiAgICBhcHBsaWNhdGlvbiBWQVJDSEFSKDI1NSkgTk9UIE5VTEwsCiAgICBlbnZpcm9ubWVudCBWQVJDSEFSKDI1NSksCiAgICBob3N0IFZBUkNIQVIoMjU1KSBOT1QgTlVMTCwKICAgIHBvcnQgSU5URUdFUiBOT1QgTlVMTCwKICAgIGNhcGFiaWxpdHkgSlNPTiBOT1QgTlVMTCwKICAgIHN0YXR1cyBWQVJDSEFSKDMxKSBOT1QgTlVMTCwKICAgIHVwZGF0ZWQgVElNRVNUQU1QKDMpIE5PVCBOVUxMLAogICAgUFJJTUFSWSBLRVkgKGlpZCksCiAgICBJTkRFWCBpZHhfcmlfbmFtZXNwYWNlIChuYW1lc3BhY2UpLAogICAgSU5ERVggaWR4X3JpX2FwcGxpY2F0aW9uIChhcHBsaWNhdGlvbiksCiAgICBJTkRFWCBpZHhfcmlfZW52aXJvbm1lbnQgKGVudmlyb25tZW50KSwKICAgIElOREVYIGlkeF9yaV9ob3N0IChob3N0KQopOw==")
                                .build()
                )
        );
        componentRegistry.initializeComponent(
                SimpleComponentConfig.of(
                        "Hikari3xDataSource",
                        "src.data.source",
                        MapBuilder.linkedHashMap()
                                .kv("driver.class.name", "com.mysql.cj.jdbc.Driver")
                                .kv("jdbc.url", "jdbc:mysql://localhost:3306/test")
                                .kv("username", "root")
                                .kv("password", "password")
                                .build()
                )
        );
    }

    @Test
    public void test() {
        DataSource dataSource = componentRegistry.getComponent("src.data.source");
        InstanceManager instanceManager = new InstanceManager(dataSource, "system");

        Instance i1 = new Instance();
        i1.setId(UUID.randomUUID().toString());
        i1.setNamespace("test");
        i1.setApplication("test");
        i1.setEnvironment("QA");
        i1.setHost("localhost");
        i1.setPort(9090);
        i1.setCapability(
                MapBuilder.linkedHashMap()
                        .kv("type", "ApacheFlinkJobSubmission")
                        .kv("processingEngine", "ApacheFlink")
                        .kv("version", "1.13.5")
                        .build()
        );
        instanceManager.registerInstance(i1);

        Instance i2 = new Instance();
        i2.setId(UUID.randomUUID().toString());
        i2.setNamespace("test");
        i2.setApplication("test");
        i2.setEnvironment("QA");
        i2.setHost("localhost");
        i2.setPort(9090);
        i2.setCapability(
                MapBuilder.linkedHashMap()
                        .kv("type", "ApacheFlinkJobSubmission")
                        .kv("processingEngine", "ApacheFlink")
                        .kv("version", "1.13.5")
                        .build()
        );
        instanceManager.registerInstance(i2);

        List<Instance> instanceList = instanceManager.findInstances(Collections.emptyMap(), 100, 0);
        assertEquals(2, instanceList.size());

        instanceManager.unregisterInstance(i1);
        instanceManager.unregisterInstance(i2);

        instanceList = instanceManager.findInstances(Collections.emptyMap(), 100, 0);
        assertEquals(0, instanceList.size());
    }

}