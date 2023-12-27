package io.aftersound.weave.service.message;

import io.aftersound.weave.common.Severity;

public class Message extends io.aftersound.weave.common.Message {

    private Integer statusCode;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public static Message serviceError(String code, String message) {
        Message msg = new Message();
        msg.setCode(code);
        msg.setCategory(Category.SERVICE.name());
        msg.setSeverity(Severity.Error);
        msg.setContent(message);
        return msg;
    }

    public static Message serviceWarning(String code, String message) {
        Message msg = new Message();
        msg.setCode(code);
        msg.setCategory(Category.SERVICE.name());
        msg.setSeverity(Severity.Warning);
        msg.setContent(message);
        return msg;
    }

    public Message asWarning() {
        Message msg = new Message();
        msg.setStatusCode(statusCode);
        msg.setCode(getCode());
        msg.setCategory(getCategory());
        msg.setSeverity(Severity.Warning);
        msg.setContent(getContent());
        return msg;
    }
}
