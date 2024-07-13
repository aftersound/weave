package io.aftersound.weave.util.map;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class PathParser {

    public Path parse(String path) {
        return Path.of(parseSegments(path));
    }

    private List<Segment> parseSegments(String path) {
        final String p = path.trim();
        if (p.length() == 0) {
            throw new IllegalArgumentException(String.format("Path query '%s' is invalid", path));
        }

        List<Segment> segments = new ArrayList<>();

        ParsingTracker parsingTracker = new ParsingTracker();
        TextBasedSegmentBuilder segmentBuilder = new TextBasedSegmentBuilder();

        for (int index = 0; index < p.length(); index++) {
            char c = p.charAt(index);
            switch (c) {
                case '.': {
                    if (parsingTracker.getBracketPairCount() == 0) {
                        // .p1.p2 is not valid
                        if (segmentBuilder.getNameExprLength() == 0) {
                            throw new IllegalArgumentException(String.format("Path query '%s' is invalid", path));
                        }

                        Segment segment = segmentBuilder.build();
                        if (segment != null) {
                            segments.add(segment);
                        } else {
                            throw new IllegalArgumentException(String.format("Path query '%s' is invalid", path));
                        }

                        // set up for next part
                        segmentBuilder = new TextBasedSegmentBuilder();
                    } else {
                        segmentBuilder.appendToListQueryExpr(c);
                    }
                    break;
                }
                case '[': {
                    parsingTracker.increaseBracketPairCount();

                    if (parsingTracker.getBracketPairCount() != 1) {
                        throw new IllegalArgumentException(String.format("Path query '%s' is invalid, bracket is not paired properly", path));
                    }

                    break;
                }
                case ']': {
                    parsingTracker.decreaseBracketPairCount();

                    if (parsingTracker.getBracketPairCount() != 0) {
                        throw new IllegalArgumentException(String.format("Path query '%s' is invalid, bracket is not paired properly", path));
                    }

                    break;
                }
                default: {
                    if (parsingTracker.getBracketPairCount() == 1) {
                        segmentBuilder.appendToListQueryExpr(c);
                    } else {
                        segmentBuilder.appendToNameExpr(c);
                    }
                }
            }
        }

        if (parsingTracker.getBracketPairCount() != 0) {
            throw new IllegalArgumentException(String.format("Path query '%s' is invalid because of malformed directives", path));
        }

        Segment segment = segmentBuilder.build();
        if (segment != null) {
            segments.add(segment);
        }

        return segments;

    }

    private static class TextBasedSegmentBuilder {

        private static final Pattern COMMA_SPLITTER = Pattern.compile(",");

        private StringBuilder nameExprBuilder = new StringBuilder();
        private StringBuilder directivesExprBuilder = new StringBuilder();

        public TextBasedSegmentBuilder appendToNameExpr(char c) {
            nameExprBuilder.append(c);
            return this;
        }

        public TextBasedSegmentBuilder appendToListQueryExpr(char c) {
            directivesExprBuilder.append(c);
            return this;
        }

        public int getNameExprLength() {
            return nameExprBuilder.length();
        }

        public Segment build() {
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

            Directives directives = null;
            String directivesQueryExpr = directivesExprBuilder.toString().trim();
            if (directivesQueryExpr.length() > 0) {
                Filter filter;
                Return returnDirective;

                if ("*".equals(directivesQueryExpr)) {
                    filter = new Filter("*");
                    returnDirective = Return.allMatched();
                } else if (directivesQueryExpr.charAt(0) == '(') {
                    // expected formats
                    //  (filter condition expression)|S
                    //  (filter condition expression)|S:E

                    // find the position of right most right parenthesis
                    int prmrp = directivesQueryExpr.lastIndexOf(')');
                    if (prmrp < 0) {
                        throw new IllegalArgumentException(String.format("List query expression '%s' is invalid", directivesQueryExpr));
                    }
                    filter = new Filter(directivesQueryExpr.substring(0, prmrp + 1));

                    if (prmrp < directivesQueryExpr.length() - 1) {
                        String remainingExpr = directivesQueryExpr.substring(prmrp + 1).trim();
                        if (remainingExpr.charAt(0) != '|') {
                            throw new IllegalArgumentException(String.format("List query expression '%s' is invalid", directivesQueryExpr));
                        }
                        String returnExpr = remainingExpr.substring(1);
                        returnDirective = parseReturnDirective(returnExpr);
                    } else {
                        returnDirective = Return.allMatched();
                    }
                } else {
                    // list query format: [index] or [S:E]
                    filter = new Filter("*");
                    returnDirective = parseReturnDirective(directivesQueryExpr);
                }

                directives = new Directives();
                directives.set(Key.LIST_FILTER, filter.compile());
                directives.set(Key.LIST_RETURN, returnDirective);
            }

            return Segment.builderEitherOr(name, names).withDirectives(directives).build();
        }

        private Return parseReturnDirective(String returnExpr) {
            // find the position of first colon ':'
            int pfc = returnExpr.indexOf(':');
            if (pfc >= 0) {
                String s = returnExpr.substring(0, pfc).trim();
                String e = returnExpr.substring(pfc + 1).trim();
                int start = s.isEmpty() ? 0: Integer.parseInt(s);
                int end = e.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(e);
                return Return.inRange(start, end);
            } else {
                if ("A".equalsIgnoreCase(returnExpr)) {
                    return Return.allMatched();
                }
                if ("F".equalsIgnoreCase(returnExpr)) {
                    return Return.firstMatched();
                }
                if ("L".equalsIgnoreCase(returnExpr)) {
                    return Return.lastMatched();
                }
                int index = Integer.parseInt(returnExpr);
                return Return.atIndex(index);
            }
        }
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
