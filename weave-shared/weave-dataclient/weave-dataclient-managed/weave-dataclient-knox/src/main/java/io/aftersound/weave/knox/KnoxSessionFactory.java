package io.aftersound.weave.knox;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.dataclient.DataClientFactory;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.dataclient.Endpoint;
import org.apache.knox.gateway.shell.KnoxSession;

import java.util.Map;

public class KnoxSessionFactory extends DataClientFactory<KnoxSession> {

    public static final NamedType<Endpoint> COMPANION_CONTROL_TYPE = NamedType.of("KNOX", Endpoint.class);
    public static final NamedType<Object> COMPANION_PRODUCT_TYPE = NamedType.of("KNOX", KnoxSession.class);

    public KnoxSessionFactory(DataClientRegistry dataClientRegistry) {
        super(dataClientRegistry);
    }

    @Override
    protected KnoxSession createDataClient(Map<String, Object> options) {
        return null;
    }

    @Override
    protected void destroyDataClient(KnoxSession knoxSession) {

    }
}
