package io.aftersound.weave.batch.knox;

import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;
import io.aftersound.weave.common.NamedType;

public class KnoxDataSourceControl extends DataSourceControl {

    public static final NamedType<DataSourceControl> TYPE = NamedType.of("KNOX", KnoxDataSourceControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

}
