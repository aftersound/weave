package io.aftersound.weave.common;

import java.io.Serializable;

public class Validation implements Serializable {

    private String predicate;
    private Message error;

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }

    public Message getError() {
        return error;
    }

    public void setError(Message error) {
        this.error = error;
    }

}
