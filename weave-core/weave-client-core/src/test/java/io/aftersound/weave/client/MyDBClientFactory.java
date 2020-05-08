package io.aftersound.weave.client;

import io.aftersound.weave.common.NamedType;

public class MyDBClientFactory extends ClientFactory<MyDBClient> {

    public static final NamedType<Endpoint> COMPANINON_CONTROL_TYPE = NamedType.of("MyDB", Endpoint.class);
    public static final NamedType<Object> COMPANINON_PRODUCT_TYPE = NamedType.of("MyDB", MyDBClient.class);

    MyDBClientFactory(ClientRegistry dataClientRegistry) {
        super(dataClientRegistry);
    }

    @Override
    protected MyDBClient createDataClient(Endpoint endpoint) {
        return new MyDBClient();
    }

    @Override
    protected void destroyDataClient(MyDBClient myDBClient) {
        // Do nothing
    }
}
