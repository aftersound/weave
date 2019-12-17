package io.aftersound.weave.zk;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.zookeeper.server.admin.AdminServer;
import org.apache.zookeeper.server.admin.AdminServerFactory;
import org.apache.zookeeper.server.admin.DummyAdminServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This customization makes it possible for every ZooKeeper server to have an admin server
 * when more than 1 ZooKeeper server exists on same machine.
 */
public class ZKCustomization {

    private static final Logger LOG = LoggerFactory.getLogger(AdminServerFactory.class);

    /**
     * This works only when it runs in a JDK environment, not in JRE environment
     * ByteBuddyAgent.install(); needs to be invoked before calling init.
     */
    public static void init() {
        new ByteBuddy()
                .redefine(org.apache.zookeeper.server.admin.AdminServerFactory.class)
                .method(ElementMatchers.named("createAdminServer"))
                .intercept(MethodDelegation.to(CustomizedAdminServerFactory.class))
                .make()
                .load(
                        org.apache.zookeeper.server.admin.AdminServerFactory.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent()
                );
    }

    public static class CustomizedAdminServerFactory {

        public static AdminServer intercept() {
            ZKServer.AdminServerConfig config = ZKAdminServerConfigHolder.get();
            if (config.isEnabled()) {
                try {
                    Class<?> jettyAdminServerC = Class.forName("org.apache.zookeeper.server.admin.JettyAdminServer");
                    Object adminServer = jettyAdminServerC
                            .getConstructor(
                                    String.class,
                                    Integer.TYPE,
                                    Integer.TYPE,
                                    String.class
                            )
                            .newInstance(
                                    config.getServerAddress(),
                                    config.getServerPort(),
                                    config.getIdleTimeout(),
                                    config.getCommandURL()
                            );
                    LOG.info(
                            "Admin server is available at {}:{}{}",
                            config.getServerAddress(),
                            config.getServerPort(),
                            config.getCommandURL()
                    );
                    return (AdminServer) adminServer;
                } catch (Exception e) {
                    LOG.warn("Unable to start JettyAdminServer", e);
                }
            }
            return new DummyAdminServer();
        }
    }

}
