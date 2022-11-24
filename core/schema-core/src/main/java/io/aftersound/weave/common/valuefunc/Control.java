package io.aftersound.weave.common.valuefunc;

import java.io.Serializable;
import java.util.Arrays;
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

    public Boolean getOptional() {
        return optional;
    }

    public String getType() {
        return type;
    }

    public List<String> getAcceptedValues() {
        return acceptedValues;
    }

    public static Builder builder(String type, String description) {
        return new Builder(type, description);
    }

    public static class Builder {

        private final String type;
        private final String description;

        private Boolean optional;
        private String[] acceptedValues;

        private Builder(String type, String description) {
            this.type = type;
            this.description = description;
        }

        public Builder asOptional() {
            this.optional = Boolean.TRUE;
            return this;
        }

        public Builder withAcceptedValues(String... acceptedValues) {
            this.acceptedValues = acceptedValues;
            return this;
        }

        public Control build() {
            Control control = new Control();
            control.type = type;
            control.description = description;
            control.optional = optional;
            control.acceptedValues = acceptedValues != null ? Arrays.asList(acceptedValues) : null;
            return control;
        }

    }
}
