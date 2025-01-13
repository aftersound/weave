package io.aftersound.expr;

import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Expression which can be used to represent filter, predicate, etc.
 */
public class Expr {

    private final String operator;

    private final List<Expr> operands;

    private final String field;
    private final Object value;

    private final List<Object> values;

    private Expr(String operator, List<Expr> operands) {
        this.operator = operator;
        this.operands = operands;

        this.field = null;
        this.value = null;
        this.values = null;
    }

    private Expr(String operator, Expr operand) {
        if (operand == null) {
            throw new IllegalArgumentException("operand cannot be null");
        }

        this.operator = operator;
        this.operands = List.of(operand);

        this.field = null;
        this.value = null;
        this.values = null;
    }

    private Expr(String operator, String field, Object value) {
        this.operator = operator;
        this.field = field;
        this.value = value;

        this.operands = null;
        this.values = null;
    }

    private Expr(String operator, String field, List<Object> values) {
        this.operator = operator;
        this.field = field;
        this.values = values;

        this.value = null;
        this.operands = null;
    }

    public String getOperator() {
        return operator;
    }

    public List<Expr> getOperands() {
        return Collections.unmodifiableList(operands);
    }

    public String getField() {
        return field;
    }

    public Object getValue() {
        return value;
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(values);
    }

    @Override
    public String toString() {
        return new ToTextualExpr().apply(this);
    }

    public static <T> Expr compare(String op, String field, T value) {
        return new Expr("EQ", field, value);
    }

    public static Expr and(Expr... operands) {
        return new Expr("AND", List.of(operands));
    }

    public static Expr and(List<Expr> operands) {
        return new Expr("AND", operands);
    }

    public static <T> Expr eq(String field, T value) {
        return new Expr("EQ", field, value);
    }

    public static <T> Expr ge(String field, T value) {
        return new Expr("GE", field, value);
    }

    public static <T> Expr gt(String field, T value) {
        return new Expr("GT", field, value);
    }

    public static <T> Expr in(String field, List<Object> values) {
        return new Expr("IN", field, values);
    }

    public static <T> Expr le(String field, T value) {
        return new Expr("LE", field, value);
    }

    public static <T> Expr lt(String field, T value) {
        return new Expr("LT", field, value);
    }

    public static <T> Expr ne(String field, T value) {
        return new Expr("NE", field, value);
    }

    public static Expr not(Expr operand) {
        return new Expr("NOT", operand);
    }

    public static <T> Expr notIn(String field, List<Object> values) {
        return new Expr("NOTIN", field, values);
    }

    public static Expr or(Expr... operands) {
        return new Expr("OR", List.of(operands));
    }

    public static Expr or(List<Expr> operands) {
        return new Expr("OR", operands);
    }

    public <E> E translate(Function<Expr, E> translator) {
        return translator.apply(this);
    }

    public static class ToTextualExpr implements Function<Expr, String> {

        @Override
        public String apply(Expr expr) {
            String operator = expr.getOperator();
            switch (expr.getOperator()) {
                case "AND": {
                    StringJoiner sj = new StringJoiner(",");
                    for (Expr operand : expr.getOperands()) {
                        sj.add(operand.toString());
                    }
                    return "AND(" + sj + ")";
                }
                case "IN": {
                    StringJoiner sj = new StringJoiner(",");
                    sj.add(expr.getField());
                    for (Object v : expr.getValues()) {
                        sj.add(v.toString());
                    }
                    return "IN(" + sj + ")";
                }
                case "NOTIN": {
                    StringJoiner sj = new StringJoiner(",");
                    sj.add(expr.getField());
                    for (Object v : expr.getValues()) {
                        sj.add(v.toString());
                    }
                    return "NOTIN(" + sj + ")";
                }
                case "NOT": {
                    return "NOT(" + expr.getOperands().get(0).toString() + ")";
                }
                case "OR": {
                    StringJoiner sj = new StringJoiner(",");
                    for (Expr operand : expr.getOperands()) {
                        sj.add(operand.toString());
                    }
                    return "OR(" + sj + ")";
                }
                default: {
                    return operator + "(" + expr.getField() + "," + expr.getValue() + ")";
                }
            }
        }

    }

}
