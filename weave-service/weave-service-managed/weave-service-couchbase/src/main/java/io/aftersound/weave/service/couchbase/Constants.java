package io.aftersound.weave.service.couchbase;

import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.service.resources.ResourceType;

class Constants {

    static final ResourceType<DataClientRegistry> DATA_CLIENT_REGISTRY_RESOURCE_TYPE = new ResourceType(
            DataClientRegistry.class.getName(),
            DataClientRegistry.class
    );

}
