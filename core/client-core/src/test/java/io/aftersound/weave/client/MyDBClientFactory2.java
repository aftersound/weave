package io.aftersound.weave.client;

import io.aftersound.weave.common.NamedType;

public class MyDBClientFactory2 extends ClientFactory<MyDBClient> {

    public static final NamedType<Endpoint> COMPANINON_CONTROL_TYPE = NamedType.of("MyDB2", Endpoint.class);
    public static final NamedType<Object> COMPANINON_PRODUCT_TYPE = NamedType.of("MyDB2", MyDBClient.class);

    MyDBClientFactory2(ClientRegistry dataClientRegistry) {
        super(dataClientRegistry);
    }

    @Override
    protected MyDBClient createClient(Endpoint endpoint) {
        return new MyDBClient();
    }

    @Override
    protected void destroyClient(MyDBClient myDBClient) {
        // Do nothing
    }
}
