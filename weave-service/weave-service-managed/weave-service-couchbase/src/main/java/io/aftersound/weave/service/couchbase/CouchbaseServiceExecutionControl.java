package io.aftersound.weave.service.couchbase;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.couchbase.Repository;
import io.aftersound.weave.service.metadata.ExecutionControl;

public class CouchbaseServiceExecutionControl implements ExecutionControl {

    public static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "Couchbase",
            CouchbaseServiceExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private Repository repository;

    private ByKey byKey;
    private ByN1QL byN1QL;
    private ByViewQuery byViewQuery;

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public ByKey getByKey() {
        return byKey;
    }

    public void setByKey(ByKey byKey) {
        this.byKey = byKey;
    }

    public ByN1QL getByN1QL() {
        return byN1QL;
    }

    public void setByN1QL(ByN1QL byN1QL) {
        this.byN1QL = byN1QL;
    }

    public ByViewQuery getByViewQuery() {
        return byViewQuery;
    }

    public void setByViewQuery(ByViewQuery byViewQuery) {
        this.byViewQuery = byViewQuery;
    }
}
