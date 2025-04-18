package io.aftersound.service.message;

import io.aftersound.msg.Message;
import io.aftersound.msg.Severity;

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

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Create error message with given parameter values
     *
     * @param paramValues
     * @return error message
     */
    public Message error(final Object... paramValues) {
        return Message.builder()
                .withCode(code)
                .withCategory(category)
                .withSeverity(Severity.ERROR)
                .withContent(createMessage(paramValues))
                .build();
    }

    /**
     * Create warning message with given parameter values
     *
     * @param paramValues
     * @return error message
     */
    public Message warning(final Object... paramValues) {
        return Message.builder()
                .withCode(code)
                .withCategory(category)
                .withSeverity(Severity.WARNING)
                .withContent(createMessage(paramValues))
                .build();
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
