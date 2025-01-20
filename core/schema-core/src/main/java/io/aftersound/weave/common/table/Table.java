package io.aftersound.weave.common.table;

import java.util.List;

public class Table {

    private String type;
    private String name;
    private List<ColumnDefinition> columns;
    private List<List<Cell>> rows;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ColumnDefinition> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnDefinition> columns) {
        this.columns = columns;
    }

    public List<List<Cell>> getRows() {
        return rows;
    }

    public void setRows(List<List<Cell>> rows) {
        this.rows = rows;
    }

}
