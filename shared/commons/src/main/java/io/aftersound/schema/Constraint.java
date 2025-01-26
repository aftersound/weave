package io.aftersound.schema;

import io.aftersound.msg.Message;

import java.io.Serializable;

public class Constraint implements Serializable {

    public enum Type {

        /**
         * For request parameters which are required.
         */
        Required,

        /**
         * For request parameters which are required only when other parameter(s) is present or missing.
         */
        SoftRequired,

        /**
         * For request parameters which are optional
         */
        Optional
    }

    public static class When {

        private String condition;
        private Message explanation;

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public Message getExplanation() {
            return explanation;
        }

        public void setExplanation(Message explanation) {
            this.explanation = explanation;
        }

    }

    private Type type;

    // Optional, only matters to soft required
    private When when;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public When getWhen() {
        return when;
    }

    public void setWhen(When when) {
        this.when = when;
    }

    public static Constraint required() {
        Constraint constraint = new Constraint();
        constraint.setType(Type.Required);
        return constraint;
    }

    public static Constraint optional() {
        Constraint constraint = new Constraint();
        constraint.setType(Type.Optional);
        return constraint;
    }

    public static Constraint softRequired(When when) {
        Constraint constraint = new Constraint();
        constraint.setType(Type.SoftRequired);
        constraint.setWhen(when);
        return constraint;
    }
}
