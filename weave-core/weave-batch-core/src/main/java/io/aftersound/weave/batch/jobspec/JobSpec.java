package io.aftersound.weave.batch.jobspec;

import io.aftersound.weave.metadata.Control;

public abstract class JobSpec implements Control, Copiable<JobSpec> {

    private String id;
    private String classification;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
}
