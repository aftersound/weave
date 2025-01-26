package io.aftersound.schema;

import io.aftersound.func.Func;
import io.aftersound.func.FuncFactory;
import io.aftersound.msg.Message;

/**
 * Function directive
 */
public class Directive {

    /**
     * the label, which is used to specify the target or context that
     * this directive is expected to be applied.
     */
    private String label;

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
    public <IN, OUT> Func<IN, OUT> getFunction() {
        return (Func<IN, OUT>) function;
    }

    public static Builder builder(String label, String func) {
        return new Builder(label, func);
    }

    public static class Builder {

        private final String label;
        private final String func;
        private Message message;

        private Builder(String label, String func) {
            this.label = label;
            this.func = func;
        }

        public Builder withMessage(Message message) {
            this.message = message;
            return this;
        }

        public Directive build() {
            Directive d = new Directive();
            d.setLabel(label);
            d.setFunc(func);
            d.setMessage(message);
            return d;
        }

    }

}
