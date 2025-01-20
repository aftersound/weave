package io.aftersound.msg;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Message implements Serializable {

    private String code;
    private String category;
    private String severity;
    private String content;
    private Map<String, Object> extraInfo;

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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Map<String, Object> getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(Map<String, Object> extraInfo) {
        this.extraInfo = extraInfo;
    }

    public Message copy() {
        return new Builder()
                .withCode(code)
                .withContent(content)
                .withCategory(category)
                .withSeverity(severity)
                .withExtraInfo(new LinkedHashMap<>(extraInfo))
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(String code, String content) {
        return new Builder().withCode(code).withContent(content);
    }

    public static class Builder {

        private String code;
        private String category;
        private String severity;
        private String content;
        private Map<String, Object> extraInfo;

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

        public Builder withSeverity(String severity) {
            this.severity = severity;
            return this;
        }

        public Builder withExtraInfo(Map<String, Object> extraInfo) {
            this.extraInfo = extraInfo;
            return this;
        }

        public Message build() {
            Message m = new Message();
            m.code = code;
            m.category = category;
            m.severity = severity;
            m.content = content;
            m.extraInfo = extraInfo;
            return m;
        }
    }

}
