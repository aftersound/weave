package io.aftersound.weave.common;

import java.io.Serializable;

public class Validation implements Serializable {

    private String condition;
    private Message message;

    private transient ValueFunc<?, Boolean> conditionFunc;

    public Validation() {
    }

    public Validation(String condition, Message message) {
        this.condition = condition;
        this.message = message;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public <T> ValueFunc<T, Boolean> conditionFunc() {
        if (conditionFunc == null) {
            conditionFunc = MasterValueFuncFactory.create(condition);
        }
        return (ValueFunc<T, Boolean>) conditionFunc;
    }

}
