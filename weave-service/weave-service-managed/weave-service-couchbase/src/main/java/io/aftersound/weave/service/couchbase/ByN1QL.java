package io.aftersound.weave.service.couchbase;

import io.aftersound.weave.data.DataFormat;

public class ByN1QL {

    private String template;
    private DataFormat dataFormat;
    private String dataType;

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public DataFormat getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(DataFormat dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }
}
