package io.aftersound.func;

import io.aftersound.msg.Message;

/**
 * Function directive
 */
public class Directive {

    public static final String TRANSFORM = "TRANSFORM";
    public static final String VALIDATION = "VALIDATION";

    /**
     * the label, which is used to uniquely specify the directive
     */
    private String label;

    /**
     * the category
     */
    private String category;

    /**
     * the func directive itself
     */
    private String func;

    /**
     * the message to produce
     */
    private Message message;

    private transient Func<?, ?> function;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getFunc() {
        return func;
    }

    public void setFunc(String func) {
        this.func = func;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void init(FuncFactory funcFactory) {
        this.function = funcFactory.create(func);
    }

    @SuppressWarnings("unchecked")
    public <IN, OUT> Func<IN, OUT> function() {
        return (Func<IN, OUT>) function;
    }

    public static Builder builder(String label, String category, String func) {
        return new Builder(label, category, func);
    }

    public static class Builder {

        private final String label;
        private final String category;
        private final String func;
        private Message message;

        private Builder(String label, String category, String func) {
            this.label = label;
            this.category = category;
            this.func = func;
        }

        public Builder withMessage(Message message) {
            this.message = message;
            return this;
        }

        public Directive build() {
            Directive d = new Directive();
            d.setLabel(label);
            d.setCategory(category);
            d.setFunc(func);
            d.setMessage(message);
            return d;
        }

    }

}
