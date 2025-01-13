package io.aftersound.expr;

import io.aftersound.dict.Dictionary;
import io.aftersound.util.TreeNode;

import java.util.function.Function;

public abstract class Translator implements Function<TreeNode, Expr> {

    protected Function<TreeNode, Expr> leader;
    protected Dictionary<?> dictionary;

    protected abstract String getOperator();

    protected final Translator bind(Dictionary<?> dictionary) {
        this.dictionary = dictionary;
        return this;
    }

    protected final Translator bind(Function<TreeNode, Expr> leader) {
        this.leader = leader;
        return this;
    }

    protected Object parseValue(String literal, String type) throws Exception {
        switch (type.toUpperCase()) {
            case "BOOLEAN": {
                return Boolean.parseBoolean(literal);
            }
            case "DOUBLE": {
                return Double.parseDouble(literal);
            }
            case "FLOAT": {
                return Float.parseFloat(literal);
            }
            case "INTEGER": {
                return Integer.parseInt(literal);
            }
            case "LONG": {
                return Long.parseLong(literal);
            }
            case "SHORT": {
                return Short.parseShort(literal);
            }
            case "STRING": {
                return literal;
            }
            default: {
                String msg = String.format(
                        "'%s' cannot be parsed to value of '%s', which is not supported",
                        literal,
                        type
                );
                throw new Exception(msg);
            }
        }
    }

}
