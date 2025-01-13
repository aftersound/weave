package io.aftersound.expr;

import io.aftersound.dict.Dictionary;
import io.aftersound.util.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class FromTreeNode implements Function<TreeNode, Expr> {

    private final Dictionary<?> dictionary;

    public FromTreeNode(Dictionary<?> dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public Expr apply(TreeNode treeNode) {
        final String op = treeNode.getData();
        switch (op) {
            case "AND": {
                return translateAND(treeNode);
            }
            case "EQ": {
                return translateEQ(treeNode);
            }
            case "GE": {
                return translateGE(treeNode);
            }
            case "GT": {
                return translateGT(treeNode);
            }
            case "IN": {
                return translateIN(treeNode);
            }
            case "LE": {
                return translateLE(treeNode);
            }
            case "LT": {
                return translateLT(treeNode);
            }
            case "NE": {
                return translateNE(treeNode);
            }
            case "NOT": {
                return translateNOT(treeNode);
            }
            case "NOTIN": {
                return translateNOTIN(treeNode);
            }
            case "OR": {
                return translateOR(treeNode);
            }
            default: {
                String msg = String.format(
                        "'%s' is malformed. Operator '%s' is not supported",
                        treeNode.toExpr(),
                        op
                );
                throw new IllegalArgumentException(msg);
            }
        }
    }

    private Expr translateAND(TreeNode expr) {
        if (expr.getChildrenCount() == 0) {
            String msg = String.format(
                    "'%s' is malformed expr. 'AND' expression takes at least 1 operand",
                    expr.toExpr()
            );
            throw new IllegalArgumentException(msg);
        }

        List<Expr> operands = new ArrayList<>(expr.getChildrenCount());
        for (TreeNode child : expr.getChildren()) {
            operands.add(apply(child));
        }
        return Expr.and(operands);
    }

    private Expr translateEQ(TreeNode expr) {
        return createComparisonFilter("EQ", expr);
    }

    private Expr translateGE(TreeNode expr) {
        return createComparisonFilter("GE", expr);
    }

    private Expr translateGT(TreeNode expr) {
        return createComparisonFilter("GT", expr);
    }

    private Expr translateIN(TreeNode expr) {
        String fieldName = expr.getChildAt(0).getData();
        List<String> literals = expr.getDataOfChildren(1);

        String fieldType = dictionary.getAttribute(fieldName, "type");
        if (fieldType == null) {
            String msg = String.format(
                    "'%s' cannot be translated because field '%s' is not in defined dictionary",
                    expr.toExpr(),
                    fieldName
            );
            throw new IllegalArgumentException(msg);
        }

        List<Object> values = new ArrayList<>(literals.size());
        for (String literal : literals) {
            Object value;
            try {
                value = parseValue(literal, fieldType);
            } catch (Exception e) {
                String msg = String.format(
                        "'%s' cannot be translated because literal '%s' cannot be parsed to type '%s'",
                        expr.toExpr(),
                        literal,
                        fieldType
                );
                throw new IllegalArgumentException(msg, e);
            }
            values.add(value);
        }

        return Expr.in(fieldName, values);
    }

    private Expr translateLE(TreeNode expr) {
        return createComparisonFilter("LE", expr);
    }

    private Expr translateLT(TreeNode expr) {
        return createComparisonFilter("LT", expr);
    }

    private Expr translateNE(TreeNode expr) {
        return createComparisonFilter("NE", expr);
    }

    private Expr translateNOT(TreeNode expr) {
        if (expr.getChildrenCount() != 1) {
            String msg = String.format(
                    "'%s' is malformed expr. 'NOT' expression takes 1 and only 1 operand",
                    expr.toExpr()
            );
            throw new IllegalArgumentException(msg);
        }

        Expr filter = apply(expr.getChildAt(0));
        return Expr.not(filter);
    }

    private Expr translateNOTIN(TreeNode expr) {
        String fieldName = expr.getChildAt(0).getData();
        List<String> literals = expr.getDataOfChildren(1);

        String fieldType = dictionary.getAttribute(fieldName, "type");
        if (fieldType == null) {
            String msg = String.format(
                    "'%s' cannot be translated because field '%s' is not in defined dictionary",
                    expr.toExpr(),
                    fieldName
            );
            throw new IllegalArgumentException(msg);
        }

        List<Object> values = new ArrayList<>(literals.size());
        for (String literal : literals) {
            Object value;
            try {
                value = parseValue(literal, fieldType);
            } catch (Exception e) {
                String msg = String.format(
                        "'%s' cannot be translated because literal '%s' cannot be parsed to type '%s'",
                        expr.toExpr(),
                        literal,
                        fieldType
                );
                throw new IllegalArgumentException(msg, e);
            }
            values.add(value);
        }

        return Expr.notIn(fieldName, values);
    }

    private Expr translateOR(TreeNode expr) {
        if (expr.getChildrenCount() == 0) {
            String msg = String.format(
                    "'%s' is malformed expr. 'OR' expression takes at least 1 operand",
                    expr.toExpr()
            );
            throw new IllegalArgumentException(msg);
        }

        List<Expr> operands = new ArrayList<>(expr.getChildrenCount());
        for (TreeNode child : expr.getChildren()) {
            operands.add(apply(child));
        }
        return Expr.or(operands);
    }

    private Expr createComparisonFilter(String op, TreeNode expr) {
        if (expr.getChildrenCount() != 2) {
            String msg = String.format(
                    "'%s' is malformed expr. '%s' expression takes 2 and only 2 operands",
                    expr.toExpr(),
                    op
            );
            throw new IllegalArgumentException(msg);
        }
        String fieldName = expr.getDataOfChildAt(0);
        String literal = expr.getDataOfChildAt(1);
        String fieldType = dictionary.getAttribute(fieldName, "type");

        if (fieldType == null) {
            String msg = String.format(
                    "'%s' cannot be translated because field '%s' is not in defined dictionary",
                    expr.toExpr(),
                    fieldName
            );
            throw new IllegalArgumentException(msg);
        }

        Object value;
        try {
            value = parseValue(literal, fieldType);
        } catch (Exception e) {
            String msg = String.format(
                    "'%s' cannot be translated because literal '%s' cannot be parsed to type '%s'",
                    expr.toExpr(),
                    literal,
                    fieldType
            );
            throw new IllegalArgumentException(msg, e);
        }

        switch (op) {
            case "EQ": {
                return Expr.eq(fieldName, value);
            }
            case "LE": {
                return Expr.le(fieldName, value);
            }
            case "LT": {
                return Expr.lt(fieldName, value);
            }
            case "GE": {
                return Expr.ge(fieldName, value);
            }
            case "GT": {
                return Expr.gt(fieldName, value);
            }
            case "NE": {
                return Expr.ne(fieldName, value);
            }
            default: {
                String msg = String.format(
                        "'%s' cannot be translated because operator '%s' is not supported",
                        expr.toExpr(),
                        op
                );
                throw new IllegalArgumentException(msg);
            }
        }

    }

    private Object parseValue(String literal, String type) throws Exception {
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
