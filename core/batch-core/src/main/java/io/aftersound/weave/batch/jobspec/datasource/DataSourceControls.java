package io.aftersound.weave.batch.jobspec.datasource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DataSourceControls {

    private final List<DataSourceControl> dscs;

    private DataSourceControls(List<DataSourceControl> dscs) {
        this.dscs= dscs;
    }

    public static DataSourceControls from(DataSourceControl... dscs) {
        return new DataSourceControls(dscs != null ? Arrays.asList(dscs) : Collections.<DataSourceControl>emptyList());
    }

    public int size() {
        return dscs.size();
    }

    @SuppressWarnings("unchecked")
    public <DSC extends DataSourceControl> DSC dataSourceControl(int index) {
        return (DSC)dscs.get(index);
    }

    public List<DataSourceControl> all() {
        return dscs;
    }

}
