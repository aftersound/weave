package io.aftersound.weave.service.message;

public class Template {

    private final long id;
    private final Category category;
    private final String messageTemplate;
    private final Param[] parameters;

    public Template(long id, Category category, String template, Param... parameters) {
        this.id = id;
        this.category = category;
        this.messageTemplate = template;
        this.parameters = parameters != null ? parameters : new Param[0];
    }

    public long getId() {
        return id;
    }

    public Message error(final Object... paramValues) {
        Message error = new Message();
        error.setSeverity(Severity.ERROR);
        error.setId(id);
        error.setCategory(category);
        error.setMessage(createMessage(paramValues));
        return error;
    }

    public Message warning(final Object... paramValues) {
        Message warning = new Message();
        warning.setSeverity(Severity.WARNING);
        warning.setId(this.id);
        warning.setCategory(this.category);
        warning.setMessage(createMessage(paramValues));
        return warning;
    }

    private String createMessage(final Object... paramValues) {
        String msg = messageTemplate;
        for (int index = 0; index < parameters.length; index++) {
            String paramValue = paramValues != null && paramValues.length > index ? String.valueOf(paramValues[index])
                    : "UNKNOWN";
            msg = msg.replaceAll("\\{" + parameters[index].getName() + "\\}", paramValue);
        }
        return msg;
    }
}
