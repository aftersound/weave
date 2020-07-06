package io.aftersound.weave.client;

import io.aftersound.weave.common.NamedType;

public class VoidClientFactory extends ClientFactory<Object> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of(
            "VOID",
            Endpoint.class
    );

    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("VOID", Object.class);

    private static final Object VOID = new Object();

    protected VoidClientFactory(ClientRegistry clientRegistry) {
        super(clientRegistry);
    }

    @Override
    protected Object createClient(Endpoint endpoint) {
        return VOID;
    }

    @Override
    protected void destroyClient(Object obj) {
    }
}
