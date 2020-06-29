package io.aftersound.weave.client;

import io.aftersound.weave.common.NamedType;

public class MyDB2ClientFactory extends ClientFactory<MyDB2Client> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("MyDB2", Endpoint.class);
    public static final NamedType<Object> COMPANINON_PRODUCT_TYPE = NamedType.of("MyDB2", MyDB2Client.class);

    MyDB2ClientFactory(ClientRegistry dataClientRegistry) {
        super(dataClientRegistry);
    }

    @Override
    protected MyDB2Client createClient(Endpoint endpoint) {
        return new MyDB2Client();
    }

    @Override
    protected void destroyClient(MyDB2Client myDBClient) {
        // Do nothing
    }
}
