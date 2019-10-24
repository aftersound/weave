package io.aftersound.weave.service.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.BinaryDocument;
import com.couchbase.client.java.document.JsonDocument;
import io.aftersound.weave.couchbase.GetControl;
import io.aftersound.weave.couchbase.Repository;
import io.aftersound.weave.data.DataFormat;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.request.ParamValueHolders;

import java.util.concurrent.TimeUnit;

class ByKeyExecutor extends Executor {

    @Override
    public Object execute(CouchbaseServiceExecutionControl executionControl, ParamValueHolders request, ServiceContext context) {
        ByKey byKey = executionControl.getByKey();

        String docKey = render(byKey.getTemplate(), request);

        Repository repository = executionControl.getRepository();
        GetControl getControl = repository.getGetControl();

        DataClientRegistry dcr = managedResources.getResource(Constants.DATA_CLIENT_REGISTRY_RESOURCE_TYPE);
        Bucket bucket = dcr.getClient(repository.getId());

        if (DataFormat.JSON == byKey.getDataFormat()) {
            JsonDocument jsonDoc;
            if (getControl == null) {
                jsonDoc = bucket.get(docKey);
            } else {
                jsonDoc = bucket.get(docKey, getControl.getTimeout(), TimeUnit.MILLISECONDS);
            }

            // TODO
            return jsonDoc.content().toString();
        }

        if (DataFormat.Smile == byKey.getDataFormat()) {
            BinaryDocument binaryDoc;

            BinaryDocument proto = BinaryDocument.create(docKey);
            if (getControl == null) {
                binaryDoc = bucket.get(proto);
            } else {
                binaryDoc = bucket.get(proto, getControl.getTimeout(), TimeUnit.MILLISECONDS);
            }

            // TODO: deserialize
            return null;
        }

        return null;
    }

}

