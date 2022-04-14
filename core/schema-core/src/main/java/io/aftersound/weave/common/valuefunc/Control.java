package io.aftersound.weave.common.valuefunc;

import java.io.Serializable;
import java.util.List;

/**
 * Describe about control parameter
 */
public class Control implements Serializable {

    private String description;
    private Boolean optional;
    private String type;
    private List<String> acceptedValues;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getOptional() {
        return optional;
    }

    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getAcceptedValues() {
        return acceptedValues;
    }

    public void setAcceptedValues(List<String> acceptedValues) {
        this.acceptedValues = acceptedValues;
    }
}
