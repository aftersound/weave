package io.aftersound.weave.batch.jobspec;

import io.aftersound.weave.batch.jobspec.datasource.DataSourceControl;

import java.util.List;

public abstract class DataSourceAwareJobSpec extends JobSpec {

    private List<DataSourceControl> dataSourceControls;

    public List<DataSourceControl> getDataSourceControls() {
        return dataSourceControls;
    }

    public void setDataSourceControls(List<DataSourceControl> dataSourceControls) {
        this.dataSourceControls = dataSourceControls;
    }
}
