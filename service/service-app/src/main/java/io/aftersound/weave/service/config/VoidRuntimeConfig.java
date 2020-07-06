package io.aftersound.weave.service.config;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.resource.ResourceConfig;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.runtime.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VoidRuntimeConfig extends ClientAndNamespaceAwareRuntimeConfig<Void> {

    public VoidRuntimeConfig(
            ClientRegistry clientRegistry,
            String clientId,
            String namespace,
            ConfigFormat configFormat,
            ConfigUpdateStrategy configUpdateStrategy) {
        super(clientRegistry, clientId, namespace, configFormat, configUpdateStrategy);
    }

    @Override
    public ConfigProvider<ActorBindingsConfig> getActorBindingsConfigProvider() {
        return new ConfigProvider<ActorBindingsConfig>() {
            @Override
            protected List<ActorBindingsConfig> getConfigList() {
                final String[][] scenarioAndBaseTypePairArray = {
                        {
                                "client.factory.types",
                                "io.aftersound.weave.client.ClientFactory"
                        },
                        {
                                "codec.factory.types",
                                "io.aftersound.weave.codec.CodecFactory"
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
                                "io.aftersound.weave.service.ServiceExecutor"
                        }
                };

                List<ActorBindingsConfig> abcList = new ArrayList<>();
                for (String[] scenarioAndBaseType : scenarioAndBaseTypePairArray) {
                    ActorBindingsConfig abc = new ActorBindingsConfig();
                    abc.setScenario(scenarioAndBaseType[0]);
                    abc.setBaseType(scenarioAndBaseType[1]);
                    abc.setExtensionTypes(Collections.EMPTY_LIST);
                    abcList.add(abc);
                }
                return Collections.unmodifiableList(abcList);
            }
        };
    }

    @Override
    public ConfigProvider<Endpoint> getClientConfigProvider() {
        return new ConfigProvider<Endpoint>() {
            @Override
            protected List<Endpoint> getConfigList() {
                return Collections.emptyList();
            }
        };
    }

    @Override
    public ConfigProvider<ServiceMetadata> getServiceMetadataProvider() {
        return new ConfigProvider<ServiceMetadata>() {
            @Override
            protected List<ServiceMetadata> getConfigList() {
                return Collections.emptyList();
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
