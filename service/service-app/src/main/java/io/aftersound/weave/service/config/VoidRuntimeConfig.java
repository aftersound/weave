package io.aftersound.weave.service.config;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.sample.extension.service.GreetingExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.Constraint;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.ParamType;
import io.aftersound.weave.service.runtime.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class VoidRuntimeConfig extends ClientAndNamespaceAwareRuntimeConfig<Void> {

    public VoidRuntimeConfig(
            ComponentRegistry componentRegistry,
            String clientId,
            String namespace,
            ConfigFormat configFormat,
            ConfigUpdateStrategy configUpdateStrategy) {
        super(componentRegistry, clientId, namespace, configFormat, configUpdateStrategy);
    }

    @Override
    public ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider() {
        return new ConfigProvider<ActorBindingsConfig>() {
            @Override
            protected List<ActorBindingsConfig> getConfigList() {
                final String[][] scenarioAndBaseTypeAndBindingsArray = {
                        {
                                "component.factory.types",
                                "io.aftersound.weave.component.ComponentFactory"
                        },
                        {
                                "codec.factory.types",
                                "io.aftersound.weave.codec.CodecFactory",
                                "io.aftersound.weave.sample.extension.codec.StringCodecFactory"
                        },
                        {
                                "cache.factory.types",
                                "io.aftersound.weave.service.cache.CacheFactory"
                        },
                        {
                                "cache.key.generator.types",
                                "io.aftersound.weave.service.cache.KeyGenerator"
                        },
                        {
                                "param.validator.types",
                                "io.aftersound.weave.service.request.Validator"
                        },
                        {
                                "param.deriver.types",
                                "io.aftersound.weave.service.request.Deriver"
                        },
                        {
                                "authenticator.types",
                                "io.aftersound.weave.service.security.Authenticator"
                        },
                        {
                                "authorizer.types",
                                "io.aftersound.weave.service.security.Authorizer"
                        },
                        {
                                "admin.resource.manager.types",
                                "io.aftersound.weave.resource.ResourceManager"
                        },
                        {
                                "admin.service.executor.types",
                                "io.aftersound.weave.service.ServiceExecutor"
                        },
                        {
                                "resource.manager.types",
                                "io.aftersound.weave.resource.ResourceManager"
                        },
                        {
                                "service.executor.types",
                                "io.aftersound.weave.service.ServiceExecutor",
                                "io.aftersound.weave.sample.extension.service.GreetingServiceExecutor"
                        }
                };

                List<ActorBindingsConfig> abcList = new ArrayList<>();
                for (String[] scenarioAndBaseTypeAndBindings : scenarioAndBaseTypeAndBindingsArray) {
                    ActorBindingsConfig abc = new ActorBindingsConfig();
                    abc.setScenario(scenarioAndBaseTypeAndBindings[0]);
                    abc.setBaseType(scenarioAndBaseTypeAndBindings[1]);
                    if (scenarioAndBaseTypeAndBindings.length > 2) {
                        String[] extensionTypes = Arrays.copyOfRange(
                                scenarioAndBaseTypeAndBindings,
                                2,
                                scenarioAndBaseTypeAndBindings.length
                        );
                        abc.setExtensionTypes(Arrays.asList(extensionTypes));
                    }

                    abcList.add(abc);
                }
                return Collections.unmodifiableList(abcList);
            }
        };
    }

    @Override
    public ConfigProvider<ComponentConfig> getComponentConfigProvider() {
        return new ConfigProvider<ComponentConfig>() {
            @Override
            protected List<ComponentConfig> getConfigList() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public ConfigProvider<ServiceMetadata> getServiceMetadataProvider() {
        return new ConfigProvider<ServiceMetadata>() {
            @Override
            protected List<ServiceMetadata> getConfigList() {
                ServiceMetadata serviceMetadata = new ServiceMetadata();

                serviceMetadata.setPath("/greeting/{name}");

                ParamField p1Field = new ParamField();
                p1Field.setName("p1");
                p1Field.setValueSpec("String");
                p1Field.setType(ParamType.Path);
                Constraint p1Constraint = new Constraint();
                p1Constraint.setType(Constraint.Type.Required);
                p1Field.setConstraint(p1Constraint);

                ParamField nameField = new ParamField();
                nameField.setName("name");
                nameField.setValueSpec("String");
                nameField.setType(ParamType.Path);
                Constraint nameConstraint = new Constraint();
                nameConstraint.setType(Constraint.Type.Required);
                nameField.setConstraint(p1Constraint);

                serviceMetadata.setParamFields(
                        Arrays.asList(p1Field, nameField)
                );

                GreetingExecutionControl executionControl = new GreetingExecutionControl();
                executionControl.setGreetingWords(
                        Arrays.asList(
                                "您好",
                                "Hello",
                                "¡Hola",
                                "Aloha",
                                "Bonjour",
                                "Hallo",
                                "Ciao",
                                "こんにちは",
                                "안영하세요"
                        )
                );

                serviceMetadata.setExecutionControl(executionControl);

                return Collections.singletonList(serviceMetadata);
            }
        };
    }

    @Override
    public ConfigProvider<ResourceConfig> getResourceConfigProvider() {
        return new ConfigProvider<ResourceConfig>() {
            @Override
            protected List<ResourceConfig> getConfigList() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public ConfigProvider<ServiceMetadata> getAdminServiceMetadataProvider() {
        return new ConfigProvider<ServiceMetadata>() {
            @Override
            protected List<ServiceMetadata> getConfigList() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public ConfigProvider<ResourceConfig> getAdminResourceConfigProvider() {
        return new ConfigProvider<ResourceConfig>() {
            @Override
            protected List<ResourceConfig> getConfigList() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public ConfigProvider<ResourceDeclarationOverride> getAdminResourceDeclarationOverrideConfigProvider() {
        return new ConfigProvider<ResourceDeclarationOverride>() {
            @Override
            protected List<ResourceDeclarationOverride> getConfigList() {
                return Collections.emptyList();
            }
        };
    }
}
