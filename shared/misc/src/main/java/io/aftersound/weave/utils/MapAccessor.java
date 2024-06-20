package io.aftersound.weave.utils;

import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MapAccessor {

    private final Map<String, Object> m;

    public MapAccessor(Map<String, Object> m) {
        this.m = m;
    }

    public <T> T query(String path) {
        return null;
    }

    private enum ReturnDirective {
        AllMatched,
        FirstMatched,
        LastMatched
    }

    private static class Predicate {

        private final String expr;

        private CompiledExpression compiledExpression;

        public Predicate(String expr) {
            this.expr = expr;
        }

        public Predicate compile() {
            this.compiledExpression = new ExpressionCompiler(expr).compile();
            return this;
        }

        public boolean apply(Map<String, Object> variables) {
            Boolean r = MVEL.executeExpression(compiledExpression, variables, Boolean.class);
            return r != null ? r.booleanValue() : false;
        }

    }

    private static class Directive {

        private Integer index;
        private Predicate predicate;
        private ReturnDirective returnDirective;

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }

        public Predicate getPredicate() {
            return predicate;
        }

        public void setPredicate(Predicate predicate) {
            this.predicate = predicate;
        }

        public ReturnDirective getReturnDirective() {
            return returnDirective;
        }

        public void setReturnDirective(ReturnDirective returnDirective) {
            this.returnDirective = returnDirective;
        }
    }

    private static class Part {

        private String name;

        private List<String> names;

        private Directive directive;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getNames() {
            return names;
        }

        public void setNames(List<String> names) {
            this.names = names;
        }

        public Directive getDirective() {
            return directive;
        }

        public void setDirective(Directive directive) {
            this.directive = directive;
        }
    }

    private static class TextBasedPartBuilder {

        private static final Pattern COMMA_SPLITTER = Pattern.compile(",");

        private StringBuilder nameExprBuilder = new StringBuilder();
        private StringBuilder directiveExprBuilder = new StringBuilder();

        public TextBasedPartBuilder appendToNameExpr(char c) {
            nameExprBuilder.append(c);
            return this;
        }

        public TextBasedPartBuilder appendToDirectiveExpr(char c) {
            directiveExprBuilder.append(c);
            return this;
        }

        public int getNameExprLength() {
            return nameExprBuilder.length();
        }

        public int getDirectiveExprLength() {
            return directiveExprBuilder.length();
        }

        public Part build() {
            String name = null;
            List<String> names = null;

            final String nameExpr = nameExprBuilder.toString().trim();
            if (nameExpr.length() == 0) {
                return null;
            }

            if (nameExpr.startsWith("{")) {
                if (nameExpr.endsWith("}")) {
                    names = new ArrayList<>();
                    String[] nameArray = COMMA_SPLITTER.split(nameExpr.substring(1, nameExpr.length() - 1));
                    for (String n : nameArray) {
                        n = n.trim();
                        if (n.length() > 0) {
                            names.add(n);
                        }
                    }
                    if (names.isEmpty()) {
                        throw new IllegalArgumentException(String.format("'%s' is invalid", nameExpr));
                    }
                } else {
                    throw new IllegalArgumentException(String.format("'%s' is invalid", nameExpr));
                }
            } else {
                name = nameExpr;
            }

            Directive directive = null;

            String directiveExpr = directiveExprBuilder.toString().trim();
            if (directiveExpr.length() > 0) {
                Integer index = null;
                ReturnDirective returnDirective = null;
                Predicate predicate = null;

                final char ch = directiveExpr.charAt(0);
                try {
                    if (ch == '(') {
                        final char cl = directiveExpr.charAt(directiveExpr.length() - 1);   // last char
                        switch (cl) {
                            case ')': {
                                predicate = new Predicate(directiveExpr);
                                returnDirective = ReturnDirective.AllMatched;
                                break;
                            }
                            case '@': {
                                String e = directiveExpr.substring(0, directiveExpr.length() - 1);
                                predicate = new Predicate(e);
                                returnDirective = ReturnDirective.AllMatched;
                                break;
                            }
                            case '$': {
                                String e = directiveExpr.substring(0, directiveExpr.length() - 1);
                                predicate = new Predicate(e);
                                returnDirective = ReturnDirective.FirstMatched;
                                break;
                            }
                            case '!': {
                                String e = directiveExpr.substring(0, directiveExpr.length() - 1);
                                predicate = new Predicate(e);
                                returnDirective = ReturnDirective.LastMatched;
                                break;
                            }
                            default: {
                                throw new RuntimeException();
                            }
                        }
                    } else if (ch == '*') {
                        predicate = new Predicate("true");
                        returnDirective = ReturnDirective.AllMatched;
                    } else if (ch >= '0' && ch <= '9') {
                        index = Integer.parseInt(directiveExpr);
                    }

                    directive = new Directive();
                    directive.setIndex(index);
                    directive.setPredicate(predicate.compile());
                    directive.setReturnDirective(returnDirective);

                } catch (RuntimeException re) {
                    throw new IllegalArgumentException(
                            String.format("directive '%s' for part '%s' is invalid", directiveExpr, nameExpr),
                            re
                    );
                }
            }

            Part p = new Part();
            p.setName(name);
            p.setNames(names);
            p.setDirective(directive);
            return p;
        }
    }

    private static class PathParser {

        public List<Part> parse(String path) {
            final String p = path.trim();
            if (p.length() == 0) {
                throw new IllegalArgumentException(String.format("'%s' is not a valid path", path));
            }

            List<Part> parts = new ArrayList<>();

            ParsingTracker parsingTracker = new ParsingTracker();
            TextBasedPartBuilder partBuilder = new TextBasedPartBuilder();

            for (int index = 0; index < p.length(); index++) {
                char c = p.charAt(index);
                switch (c) {
                    case '.': {
                        if (parsingTracker.getBracketPairCount() == 0) {
                            // .p1.p2 is not valid
                            if (partBuilder.getNameExprLength() == 0) {
                                throw new IllegalArgumentException(String.format("'%s' is invalid path", path));
                            }

                            Part part = partBuilder.build();
                            if (part != null) {
                                parts.add(part);
                            } else {
                                throw new IllegalArgumentException(String.format("'%s' is invalid path", path));
                            }

                            // set up for next part
                            partBuilder = new TextBasedPartBuilder();
                        }
                        break;
                    }
                    case '[': {
                        parsingTracker.increaseBracketPairCount();

                        if (parsingTracker.getBracketPairCount() != 1) {
                            throw new IllegalArgumentException(String.format("'%s' is invalid path, bracket is not paired properly", path));
                        }

                        break;
                    }
                    case ']': {
                        parsingTracker.decreaseBracketPairCount();

                        if (parsingTracker.getBracketPairCount() != 0) {
                            throw new IllegalArgumentException(String.format("'%s' is invalid path, bracket is not paired properly", path));
                        }

                        break;
                    }
                    default: {
                        if (parsingTracker.getBracketPairCount() == 1) {
                            partBuilder.appendToDirectiveExpr(c);
                        } else {
                            partBuilder.appendToNameExpr(c);
                        }
                    }
                }
            }

            if (parsingTracker.getBracketPairCount() != 0) {
                throw new IllegalArgumentException(String.format("'%s' is not a valid path because of malformed directive", path));
            }

            Part part = partBuilder.build();
            if (part != null) {
                parts.add(part);
            }

            return parts;

        }

        private class ParsingTracker {

            private int bracketPairCount;

            public int getBracketPairCount() {
                return bracketPairCount;
            }

            public int increaseBracketPairCount() {
                bracketPairCount++;
                return bracketPairCount;
            }

            public int decreaseBracketPairCount() {
                bracketPairCount--;
                return bracketPairCount;
            }
        }

    }

}
