package io.aftersound.weave.service.message;

public class Message {

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

    public static Message serviceError(long id, String message) {
        Message error = new Message();
        error.setCategory(Category.SERVICE);
        error.setSeverity(Severity.ERROR);
        error.setId(id);
        error.setMessage(message);
        return error;
    }

    public static Message serviceWarning(long id, String message) {
        Message error = new Message();
        error.setCategory(Category.SERVICE);
        error.setSeverity(Severity.WARNING);
        error.setId(id);
        error.setMessage(message);
        return error;
    }

    public Message asWarning() {
        Message warning = new Message();
        warning.setCategory(category);
        warning.setSeverity(Severity.WARNING);
        warning.setId(id);
        warning.setMessage(message);
        return warning;
    }
}
