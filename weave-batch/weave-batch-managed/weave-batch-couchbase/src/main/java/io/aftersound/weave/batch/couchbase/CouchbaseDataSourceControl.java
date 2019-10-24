package io.aftersound.weave.batch.couchbase;

import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.common.NamedType;

public class CouchbaseDataSourceControl extends DataSourceControl {

    public static final NamedType<DataSourceControl> TYPE = NamedType.of("couchbase", CouchbaseDataSourceControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
