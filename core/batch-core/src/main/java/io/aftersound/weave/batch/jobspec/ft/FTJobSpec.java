package io.aftersound.weave.batch.jobspec.ft;

import io.aftersound.weave.batch.WithSourceFileHandlingControl;
import io.aftersound.weave.batch.jobspec.DataSourceAwareJobSpec;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.filehandler.FileHandlingControl;

/**
 * For batch jobs which moves data file from one file system/storage to another
 */
public class FTJobSpec extends DataSourceAwareJobSpec implements WithSourceFileHandlingControl {

    public static final NamedType<JobSpec> TYPE = NamedType.of(
            "FT",
            FTJobSpec.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private FileHandlingControl sourceFileHandlingControl;
    private FileHandlingControl targetFileHandlingControl;

    @Override
    public FileHandlingControl getSourceFileHandlingControl() {
        return sourceFileHandlingControl;
    }

    @Override
    public void setSourceFileHandlingControl(FileHandlingControl sourceFileHandlingControl) {
        this.sourceFileHandlingControl = sourceFileHandlingControl;
    }

    public FileHandlingControl getTargetFileHandlingControl() {
        return targetFileHandlingControl;
    }

    public void setTargetFileHandlingControl(FileHandlingControl targetFileHandlingControl) {
        this.targetFileHandlingControl = targetFileHandlingControl;
    }

    @Override
    public JobSpec copy() {
        FTJobSpec jobSpec = new FTJobSpec();
        jobSpec.setId(this.getId());
        jobSpec.setDataSourceControls(this.getDataSourceControls());
        jobSpec.setSourceFileHandlingControl(this.getSourceFileHandlingControl());
        jobSpec.setTargetFileHandlingControl(this.getTargetFileHandlingControl());
        return jobSpec;
    }
}
