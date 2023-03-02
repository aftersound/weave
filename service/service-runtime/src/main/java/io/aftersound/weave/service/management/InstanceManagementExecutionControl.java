package io.aftersound.weave.service.management;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

import javax.sql.DataSource;

/**
 * {@link ExecutionControl} for service runtime config management
 */
public final class InstanceManagementExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "InstanceManagementService",
            InstanceManagementExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    /**
     * the id of the {@link DataSource} which has the table serves as the repository of service instance registration
     */
    private String dataSource;

    /**
     * the table which serves as the repository of service instance registration
     */
    private String table;

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String table() {
        return table != null ? table : "instance";
    }

}
