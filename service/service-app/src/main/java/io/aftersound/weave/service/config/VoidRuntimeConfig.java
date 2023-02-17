package io.aftersound.weave.service.config;

import io.aftersound.weave.actor.ActorBindingsConfig;
import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.sample.extension.service.GreetingExecutionControl;
import io.aftersound.weave.service.metadata.ServiceMetadata;
import io.aftersound.weave.service.metadata.param.Constraint;
import io.aftersound.weave.service.metadata.param.ParamField;
import io.aftersound.weave.service.metadata.param.ParamType;
import io.aftersound.weave.service.runtime.ClientAndNamespaceAwareRuntimeConfig;
import io.aftersound.weave.service.runtime.ConfigFormat;
import io.aftersound.weave.service.runtime.ConfigProvider;
import io.aftersound.weave.service.runtime.ConfigUpdateStrategy;

import java.util.*;

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
                final String[][] groupAndBaseTypeAndBindingsArray = {
                        {
                                "COMPONENT_FACTORY",
                                "io.aftersound.weave.component.ComponentFactory"
                        },
                        {
                                "VALUE_FUNC_FACTORY",
                                "io.aftersound.weave.common.ValueFuncFactory",
                                "io.aftersound.weave.service.request.ParamValueFuncFactory",
                                "io.aftersound.weave.value.CommonValueFuncFactory",
                        },
                        {
                                "ADMIN_SERVICE_EXECUTOR",
                                "io.aftersound.weave.service.ServiceExecutor"
                        },
                        {
                                "SERVICE_EXECUTOR",
                                "io.aftersound.weave.service.ServiceExecutor",
                                "io.aftersound.weave.sample.extension.service.GreetingServiceExecutor"
                        }
                };

                List<ActorBindingsConfig> abcList = new ArrayList<>();
                for (String[] scenarioAndBaseTypeAndBindings : groupAndBaseTypeAndBindingsArray) {
                    ActorBindingsConfig abc = new ActorBindingsConfig();
                    abc.setGroup(scenarioAndBaseTypeAndBindings[0]);
                    abc.setBaseType(scenarioAndBaseTypeAndBindings[1]);
                    if (scenarioAndBaseTypeAndBindings.length > 2) {
                        String[] extensionTypes = Arrays.copyOfRange(
                                scenarioAndBaseTypeAndBindings,
                                2,
                                scenarioAndBaseTypeAndBindings.length
                        );
                        abc.setTypes(Arrays.asList(extensionTypes));
                    } else {
                        abc.setTypes(Collections.emptyList());
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

                serviceMetadata.setMethods(new HashSet<>(Arrays.asList("GET")));

                ParamField p1Field = new ParamField();
                p1Field.setName("p1");
                p1Field.setType("String");
                p1Field.setValueFunc("_");
                p1Field.setParamType(ParamType.Path);
                Constraint p1Constraint = new Constraint();
                p1Constraint.setType(Constraint.Type.Required);
                p1Field.setConstraint(p1Constraint);

                ParamField nameField = new ParamField();
                nameField.setName("name");
                p1Field.setType("String");
                p1Field.setValueFunc("_");
                nameField.setParamType(ParamType.Path);
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
    public ConfigProvider<ServiceMetadata> getAdminServiceMetadataProvider() {
        return new ConfigProvider<ServiceMetadata>() {
            @Override
            protected List<ServiceMetadata> getConfigList() {
                return Collections.emptyList();
            }
        };
    }

}
