package io.aftersound.weave.batch.jobspec.etl;

import io.aftersound.weave.batch.jobspec.DataSourceAwareJobSpec;
import io.aftersound.weave.batch.jobspec.JobSpec;
import io.aftersound.weave.batch.jobspec.etl.extract.ExtractControl;
import io.aftersound.weave.batch.jobspec.etl.load.LoadControl;
import io.aftersound.weave.batch.jobspec.etl.transform.TransformControl;
import io.aftersound.weave.common.NamedType;

/**
 * For batch jobs which
 *  1.extract data directly from data source without copying data file to local,
 *      where data source is typically file system/storage
 *  2.transform
 *  3.load transformed data to target data source,
 *      where target data source could be relational database, SQL or NoSQL
 */
public class ETLJobSpec extends DataSourceAwareJobSpec {

    public static final NamedType<JobSpec> TYPE = NamedType.of(
            "ETL",
            ETLJobSpec.class
    );

    @Override
    public String getType() {
        return TYPE.name();
    }

    private ExtractControl extractControl;
    private TransformControl transformControl;
    private LoadControl loadControl;

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
        ETLJobSpec c = new ETLJobSpec();
        c.setId(this.getId());
        c.setDataSourceControls(this.getDataSourceControls());
        c.setExtractControl(this.getExtractControl());
        c.setTransformControl(this.getTransformControl());
        c.setLoadControl(this.getLoadControl());
        return c;
    }

}
