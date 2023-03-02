package io.aftersound.weave.service.management;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

import javax.sql.DataSource;

/**
 * {@link ExecutionControl} for service runtime config management
 */
public final class ApplicationManagementExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "ApplicationManagementService",
            ApplicationManagementExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    /**
     * the id of the {@link DataSource} which has the table that serves as the repository of runtime config
     */
    private String dataSource;

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

}
