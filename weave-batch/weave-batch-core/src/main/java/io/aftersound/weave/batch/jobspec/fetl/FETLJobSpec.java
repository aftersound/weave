package io.aftersound.weave.batch.jobspec.fetl;

import io.aftersound.weave.batch.WithSourceFileHandlingControl;
import io.aftersound.weave.batch.jobspec.DataSourceAwareJobSpec;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.jobspec.etl.extract.ExtractControl;
import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.batch.jobspec.etl.transform.TransformControl;
import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.filehandler.FileHandlingControl;

/**
 * For batch jobs which
 *  1.fetch data file from certain data source to local,
 *      where data source is typically file system/storage
 *  2.extract data out of data file
 *  3.transform
 *  4.load transformed data to target data source,
 *      where target data source could be relational database, SQL or NoSQL
 */
public class FETLJobSpec extends DataSourceAwareJobSpec implements WithSourceFileHandlingControl {

    public static final NamedType<JobSpec> TYPE = NamedType.of(
            "FETL",
            FETLJobSpec.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private FileHandlingControl sourceFileHandlingControl;
    private ExtractControl extractControl;
    private TransformControl transformControl;
    private LoadControl loadControl;

    @Override
    public FileHandlingControl getSourceFileHandlingControl() {
        return sourceFileHandlingControl;
    }

    @Override
    public void setSourceFileHandlingControl(FileHandlingControl sourceFileHandlingControl) {
        this.sourceFileHandlingControl = sourceFileHandlingControl;
    }

    @SuppressWarnings("unchecked")
    public <FHC extends FileHandlingControl> FHC sourceFileHandllingControl() {
        return (FHC)sourceFileHandlingControl;
    }

    public ExtractControl getExtractControl() {
        return extractControl;
    }

    @SuppressWarnings("unchecked")
    public <EC extends ExtractControl> EC extractControl() {
        return (EC)extractControl;
    }

    public void setExtractControl(ExtractControl extractControl) {
        this.extractControl = extractControl;
    }

    public TransformControl getTransformControl() {
        return transformControl;
    }

    @SuppressWarnings("unchecked")
    public <TC extends TransformControl> TC transformControl() {
        return (TC)transformControl;
    }

    public void setTransformControl(TransformControl transformControl) {
        this.transformControl = transformControl;
    }

    public LoadControl getLoadControl() {
        return loadControl;
    }

    @SuppressWarnings("unchecked")
    public <LC extends LoadControl> LC loadControl() {
        return (LC)loadControl;
    }

    public void setLoadControl(LoadControl loadControl) {
        this.loadControl = loadControl;
    }

    @Override
    public JobSpec copy() {
        FETLJobSpec c = new FETLJobSpec();
        c.setId(this.getId());
        c.setDataSourceControls(this.getDataSourceControls());
        c.setSourceFileHandlingControl(this.getSourceFileHandlingControl());
        c.setExtractControl(this.getExtractControl());
        c.setTransformControl(this.getTransformControl());
        c.setLoadControl(this.getLoadControl());
        return null;
    }

}

