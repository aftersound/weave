package io.aftersound.weave.service.message;

import io.aftersound.weave.common.Severity;

public class Template {

    private final String code;
    private final String category;
    private final String messageTemplate;
    private final Param[] parameters;

    public Template(String code, String category, String template, Param... parameters) {
        this.code = code;
        this.category = category;
        this.messageTemplate = template;
        this.parameters = parameters != null ? parameters : new Param[0];
    }

    public String getCode() {
        return code;
    }

    public Message error(final Object... paramValues) {
        Message error = new Message();
        error.setCode(code);
        error.setSeverity(Severity.Error);
        error.setCategory(category);
        error.setContent(createMessage(paramValues));
        return error;
    }

    public Message warning(final Object... paramValues) {
        Message warning = new Message();
        warning.setCode(this.code);
        warning.setSeverity(Severity.Warning);
        warning.setCategory(this.category);
        warning.setContent(createMessage(paramValues));
        return warning;
    }

    private String createMessage(final Object... paramValues) {
        String msg = messageTemplate;
        for (int index = 0; index < parameters.length; index++) {
            String paramValue = paramValues != null && paramValues.length > index ? String.valueOf(paramValues[index])
                    : "UNKNOWN";
            msg = msg.replace("{" + parameters[index].getName() + "}", paramValue);
        }
        return msg;
    }
}
