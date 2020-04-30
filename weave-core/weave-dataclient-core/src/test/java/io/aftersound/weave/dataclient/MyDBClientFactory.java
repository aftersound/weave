package io.aftersound.weave.dataclient;

import io.aftersound.weave.common.NamedType;

public class MyDBClientFactory extends DataClientFactory<MyDBClient> {

    public static final NamedType<Endpoint> COMPANINON_CONTROL_TYPE = NamedType.of("MyDB", Endpoint.class);
    public static final NamedType<Object> COMPANINON_PRODUCT_TYPE = NamedType.of("MyDB", MyDBClient.class);

    MyDBClientFactory(DataClientRegistry dataClientRegistry) {
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
