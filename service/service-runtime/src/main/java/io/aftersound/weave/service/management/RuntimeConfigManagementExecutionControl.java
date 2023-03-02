package io.aftersound.weave.service.management;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.service.metadata.ExecutionControl;

import javax.sql.DataSource;
import java.util.List;

/**
 * {@link ExecutionControl} for service runtime config management
 */
public final class RuntimeConfigManagementExecutionControl implements ExecutionControl {

    static final NamedType<ExecutionControl> TYPE = NamedType.of(
            "RuntimeConfigManagementService",
            RuntimeConfigManagementExecutionControl.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    /**
     * the id of the {@link DataSource} which has the table that serves as the repository of runtime config
     */
    private String dataSource;

    /**
     * the table which serves as the repository of runtime config
     */
    private String table;

    /**
     * Identifiers of subconfigs
     */
    private List<String> subconfigIdentifiers;

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
        return table != null ? table : "runtime_config";
    }

    public List<String> getSubconfigIdentifiers() {
        return subconfigIdentifiers;
    }

    public void setSubconfigIdentifiers(List<String> subconfigIdentifiers) {
        this.subconfigIdentifiers = subconfigIdentifiers;
    }

}
