package io.aftersound.weave.zk;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.instrument.Instrumentation;

public class ZKAgent {

    public static void premain(String arg, Instrumentation inst) {
        new AgentBuilder.Default()
                .type(ElementMatchers.named("org.apache.zookeeper.server.admin.JettyAdminServer"))
                .transform(
                        (builder, typeDescription, classLoader, module) -> {
                            builder
                                    .method(ElementMatchers.named("getInstance"))
                                    .intercept(MethodDelegation.to(ZKCustomization.CustomizedAdminServerFactory.class));
                            return builder;
                        }
                ).installOn(inst);
    }

}