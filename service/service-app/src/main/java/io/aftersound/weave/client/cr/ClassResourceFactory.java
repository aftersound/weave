package io.aftersound.weave.client.cr;

import io.aftersound.weave.client.ClientConfigUtils;
import io.aftersound.weave.client.ClientFactory;
import io.aftersound.weave.client.ClientRegistry;
import io.aftersound.weave.client.Endpoint;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.config.Settings;

import static io.aftersound.weave.client.cr.SettingsDictionary.BASE_DIRECTORY;
import static io.aftersound.weave.client.cr.SettingsDictionary.CLASS_NAME;

public class ClassResourceFactory extends ClientFactory<ClassResource> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("ClassResource", Endpoint.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("ClassResource", ClassResource.class);

    public ClassResourceFactory(ClientRegistry clientRegistry) {
        super(clientRegistry);
    }

    @Override
    protected ClassResource createClient(Endpoint endpoint) {
        Settings settings = Settings.from(
                ClientConfigUtils.extractConfig(
                        endpoint.getOptions(),
                        SettingsDictionary.CONFIG_KEYS,
                        SettingsDictionary.SECURITY_KEYS
                )
        );

        Class<?> cls;
        try {
            cls = Class.forName(settings.v(CLASS_NAME));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("failed to load class " + settings.v(CLASS_NAME), e);
        }

        return new ClassResource(cls, settings.v(BASE_DIRECTORY));
    }

    @Override
    protected void destroyClient(ClassResource classResource) {
        // Do nothing
    }
}
