package io.aftersound.weave.batch.dsv;

import io.aftersound.weave.batch.jobspec.etl.extract.ExtractControl;
import io.aftersound.weave.common.NamedType;
import org.apache.commons.csv.CSVFormat;

import java.util.List;

public class DSVExtractControl implements ExtractControl {

    public static final NamedType<ExtractControl> TYPE = NamedType.of("dsv", DSVExtractControl.class);

    @Override
    public String getType() {
        return TYPE.name();
    }

    private ArchiveFormat archiveFormat;
    private String charset;
    private char delimiter;
    private List<String> columnNames;
    private List<String> enrichedColumnNames;
    private boolean skipHeader;

    public ArchiveFormat getArchiveFormat() {
        return archiveFormat;
    }

    public CSVFormat format() {
        CSVFormat csvFormat = CSVFormat.DEFAULT;
        try {
            csvFormat = CSVFormat.newFormat(delimiter);
        } catch (Exception e) {
        }
        return csvFormat;
    }

    public void setArchiveFormat(ArchiveFormat archiveFormat) {
        this.archiveFormat = archiveFormat;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(char delimiter) {
        this.delimiter = delimiter;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(List<String> columnNames) {
        this.columnNames = columnNames;
    }

    public List<String> getEnrichedColumnNames() {
        return enrichedColumnNames;
    }

    public void setEnrichedColumnNames(List<String> enrichedColumnNames) {
        this.enrichedColumnNames = enrichedColumnNames;
    }

    public boolean isSkipHeader() {
        return skipHeader;
    }

    public void setSkipHeader(boolean skipHeader) {
        this.skipHeader = skipHeader;
    }

}
