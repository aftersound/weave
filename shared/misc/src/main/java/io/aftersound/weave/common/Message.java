package io.aftersound.weave.common;

import java.io.Serializable;

public class Message implements Serializable {
    private String code;
    private String category;
    private Severity severity;
    private String content;

    public Message() {
    }

    public Message(String code, String content) {
        this.code = code;
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Message copy() {
        return new Builder()
                .withCode(code)
                .withContent(content)
                .withCategory(category)
                .withSeverity(severity)
                .build();
    }

    public static class Builder {

        private String code;
        private String category;
        private Severity severity;
        private String content;

        private Builder() {
        }

        public Builder withCode(String code) {
            this.code = code;
            return this;
        }

        public Builder withContent(String content) {
            this.content = content;
            return this;
        }

        public Builder withCategory(String category) {
            this.category = category;
            return this;
        }

        public Builder withSeverity(Severity severity) {
            this.severity = severity;
            return this;
        }

        public Message build() {
            Message m = new Message();
            m.code = code;
            m.category = category;
            m.severity = severity;
            m.content = content;
            return m;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(String code, String content) {
        return new Builder().withCode(code).withContent(content);
    }

}
