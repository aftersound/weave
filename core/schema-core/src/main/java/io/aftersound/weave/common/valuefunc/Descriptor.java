package io.aftersound.weave.common.valuefunc;

import java.io.Serializable;
import java.util.List;

public class Descriptor implements Serializable {

    private String name;
    private List<String> controls;
    private String description;
    private List<Example> examples;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getControls() {
        return controls;
    }

    public void setControls(List<String> controls) {
        this.controls = controls;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Example> getExamples() {
        return examples;
    }

    public void setExamples(List<Example> examples) {
        this.examples = examples;
    }
}
