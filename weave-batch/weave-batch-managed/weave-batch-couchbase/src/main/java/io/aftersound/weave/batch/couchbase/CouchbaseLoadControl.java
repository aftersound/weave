package io.aftersound.weave.batch.couchbase;

import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.common.NamedType;

public class CouchbaseLoadControl implements LoadControl {

    public static final NamedType<LoadControl> TYPE = NamedType.of("couchbase", CouchbaseLoadControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }
}
