package io.aftersound.weave.service.couchbase;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.view.ViewQuery;
import com.couchbase.client.java.view.ViewResult;
import com.couchbase.client.java.view.ViewRow;
import io.aftersound.weave.couchbase.Repository;
import io.aftersound.weave.couchbase.ViewQueryControl;
import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.request.ParamValueHolders;

import java.util.concurrent.TimeUnit;

class ByViewQueryExecutor extends Executor {

    @Override
    public Object execute(CouchbaseServiceExecutionControl executionControl, ParamValueHolders request, ServiceContext context) {

        if (!validate(executionControl, context)) {
            return null;
        }

        ByViewQuery byViewQuery = executionControl.getByViewQuery();

        ViewQuery viewQuery;
        try {
            viewQuery = createViewQuery(byViewQuery.getTemplate(), request);
        } catch (Exception e) {
            context.getMessages().addMessage(Errors.VIEW_QUERY_NOT_RESOLVABLE);
            // TODO: logging
            return null;
        }

        Repository repository = executionControl.getRepository();
        ViewQueryControl vqc = repository.getViewQueryControl();

        Bucket bucket = managedResources.getResource(Constants.DATA_CLIENT_REGISTRY_RESOURCE_TYPE).getClient(repository.getId());

        ViewResult viewResult = bucket.query(viewQuery, vqc.getTimeout(), TimeUnit.MILLISECONDS);
        for (ViewRow row : viewResult) {
            RawJsonDocument doc = row.document(RawJsonDocument.class, vqc.getDocGetTimeout(), TimeUnit.MILLISECONDS);
            if (doc != null) {
                String json = doc.content();
                // TODO
            }
        }

        return null;
    }

    private boolean validate(CouchbaseServiceExecutionControl executionControl, ServiceContext context) {
        if (executionControl.getRepository().getViewQueryControl() == null) {
            context.getMessages().addMessage(Errors.VIEW_QUERY_CONTROL_MISSING);
            return false;
        }

        if (executionControl.getByViewQuery().getTemplate() == null) {
            context.getMessages().addMessage(Errors.VIEW_QUERY_TEMPLATE_MISSING);
            return false;
        }

        return true;
    }

    private ViewQuery createViewQuery(String template, ParamValueHolders request) throws Exception {
        String designView;
        try {
            designView = render(template, request);
        } catch (Exception e) {
            throw e;
        }

        if (designView == null) {
            throw new Exception();
        }

        String[] splitted = designView.split(":");
        if (splitted.length < 2) {
            throw new Exception();
        }
        String design = splitted[0];
        String view = splitted[1];
        return ViewQuery.from(design, view);
    }


}
