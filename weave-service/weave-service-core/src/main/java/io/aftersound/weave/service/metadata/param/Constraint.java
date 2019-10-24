package io.aftersound.weave.service.metadata.param;

public class Constraint {

    public enum Type {

        /**
         * For request parameters which don't need to be exposed for some reason
         */
        Predefined,

        /**
         * For request parameters which are required.
         */
        Required,

        /**
         * For request parameters which are required only when certain parameter(s) is present or missing.
         */
        SoftRequired,

        /**
         * For request parameters which are optional
         */
        Optional
    }

    public enum Condition {
        AllOtherExist,
        AnyOtherExists,
        AllOtherNotExist,
        AnyOtherNotExist
    }

    public static class When {

        private String[] otherParamNames;
        private Condition condition;

        public String[] getOtherParamNames() {
            return otherParamNames;
        }

        public void setOtherParamNames(String[] otherParamNames) {
            this.otherParamNames = otherParamNames;
        }

        public Condition getCondition() {
            return condition;
        }

        public void setCondition(Condition condition) {
            this.condition = condition;
        }

    }

    private Type type;

    // Optional, only matters to soft required
    private When requiredWhen;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public When getRequiredWhen() {
        return requiredWhen;
    }

    public void setRequiredWhen(When requiredWhen) {
        this.requiredWhen = requiredWhen;
    }

}
