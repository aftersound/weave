package io.aftersound.weave.service.message;

public class MessageData {

    private long id;

    private Severity severity;
    private Category category;
    private String message;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static MessageData serviceError(long id, String message) {
        MessageData error = new MessageData();
        error.setCategory(Category.SERVICE);
        error.setSeverity(Severity.ERROR);
        error.setId(id);
        error.setMessage(message);
        return error;
    }

    public static MessageData serviceWarning(long id, String message) {
        MessageData error = new MessageData();
        error.setCategory(Category.SERVICE);
        error.setSeverity(Severity.WARNING);
        error.setId(id);
        error.setMessage(message);
        return error;
    }

    public MessageData asWarning() {
        MessageData warning = new MessageData();
        warning.setCategory(category);
        warning.setSeverity(Severity.WARNING);
        warning.setId(id);
        warning.setMessage(message);
        return warning;
    }
}
