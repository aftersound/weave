package io.aftersound.weave.common.valuefunc;

import java.io.Serializable;

public class Example implements Serializable {

    private String expression;
    private String description;

    public String getExpression() {
        return expression;
    }

    public String getDescription() {
        return description;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String expression;
        private String description;

        private Builder() {
        }

        public Builder withExpression(String expression) {
            this.expression = expression;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Example build() {
            Example e = new Example();
            e.expression = expression;
            e.description = description;
            return e;
        }
    }
}
