package io.aftersound.weave.service.couchbase;

import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.request.ParamValueHolders;

class UnspecifiedExecutor extends Executor {

    @Override
    public Object execute(CouchbaseServiceExecutionControl executionControl, ParamValueHolders request, ServiceContext context) {
        context.getMessages().addMessage(Errors.EXECUTION_CONTROL_MALFORMED);
        return null;
    }

}
