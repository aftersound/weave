package io.aftersound.weave.service.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import io.aftersound.weave.couchbase.Repository;
import io.aftersound.weave.dataclient.DataClientRegistry;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.request.ParamValueHolders;

class ByN1QLExecutor extends Executor {

    @Override
    public Object execute(CouchbaseServiceExecutionControl executionControl, ParamValueHolders request, ServiceContext context) {
        ByN1QL byN1QL = executionControl.getByN1QL();

        String query = render(byN1QL.getTemplate(), request);

        N1qlParams n1qlParams = N1qlParams.build().adhoc(false);
        // TODO: populate parameters

        N1qlQuery n1qlQuery = N1qlQuery.simple(query, n1qlParams);

        Repository repository = executionControl.getRepository();

        DataClientRegistry dcr = managedResources.getResource(Constants.DATA_CLIENT_REGISTRY_RESOURCE_TYPE);
        Bucket bucket = dcr.getClient(repository.getId());
        N1qlQueryResult result = bucket.query(n1qlQuery);
        for (N1qlQueryRow row : result) {
            row.value();
        }

        return null;
    }

}
