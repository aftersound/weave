package io.aftersound.func;

import java.io.Serializable;

public class Example implements Serializable {

    private String expression;
    private String description;

    public String getExpression() {
        return expression;
    }

    public String getDescription() {
        return description;
    }

    public static Example as(String expression, String description) {
        Example example = new Example();
        example.expression = expression;
        example.description = description;
        return example;
    }
}
