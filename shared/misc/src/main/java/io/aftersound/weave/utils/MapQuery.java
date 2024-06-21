package io.aftersound.weave.utils;

import org.mvel2.MVEL;
import org.mvel2.compiler.CompiledExpression;
import org.mvel2.compiler.ExpressionCompiler;

import java.util.*;
import java.util.regex.Pattern;

public class MapQuery {

    private List<Segment> segments = new ArrayList<>();

    public MapQuery withSegments(List<Segment> segments) {
        if (segments != null) {
            this.segments.addAll(segments);
        }
        return this;
    }

    public MapQuery withSegment(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' cannot be null or empty");
        }
        segments.add(Segment.builder(name).build());
        return this;
    }

    public MapQuery withSegment(List<String> names) {
        if (names == null || names.isEmpty()) {
            throw new IllegalArgumentException("'names' cannot be null or empty");
        }
        segments.add(Segment.builder(names).build());
        return this;
    }

    public MapQuery withSegment(String name, ListQuery listQuery) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("'name' cannot be null or empty");
        }
        if (listQuery == null) {
            throw new IllegalArgumentException("'listQuery' cannot be null");
        }
        segments.add(Segment.builder(name).withListQuery(listQuery).build());
        return this;
    }

    public <T> T on(final Map<String, Object> m) {
        Object c = m;
        for (Segment s : segments) {
            if (c instanceof Map) {
                Map cm = (Map) c;
                if (s.getName() != null) {
                    Object o = cm.get(s.getName());
                    if (s.hasListQuery()) {
                        // List is implied
                        c = s.getListQuery().on((List) o);
                    } else {
                        c = o;
                    }
                } else {
                    c = retrieve(cm, s.getNames());
                }
            } else if (c instanceof List) {
                if (s.getName() != null) {
                    c = project((List) c, s.getName());
                } else {
                    c = project((List) c, s.getNames());
                }
            } else {
                return null;
            }
        }
        return (T) c;
    }

    private Map<String, Object> retrieve(Map s, List<String> names) {
        Map<String, Object> r = new LinkedHashMap<>(names.size());
        for (String n : names) {
            r.put(n, s.get(n));
        }
        return r;
    }

    private List project(List source, String name) {
        List l = new ArrayList();
        for (Object e : source) {
            if (e instanceof Map) {
                l.add(((Map) e).get(name));
            }
        }
        return l;
    }

    private List project(List source, List<String> names) {
        List l = new ArrayList();
        for (Object e : source) {
            if (e instanceof Map) {
                l.add(retrieve((Map) e, names));
            }
        }
        return l;
    }

    public static MapQuery parse(String pathQuery) {
        return new TextualQueryParser().parse(pathQuery);
    }

    public static class Filter  {

        private static final Set<Class<?>> BOXED_TYPES;
        static {
            Set<Class<?>> boxedTypes = new HashSet<>();
            boxedTypes.add(Boolean.class);
            boxedTypes.add(Byte.class);
            boxedTypes.add(Character.class);
            boxedTypes.add(Double.class);
            boxedTypes.add(Float.class);
            boxedTypes.add(Integer.class);
            boxedTypes.add(Long.class);
            boxedTypes.add(Short.class);
            boxedTypes.add(Void.class);
            BOXED_TYPES = Collections.unmodifiableSet(boxedTypes);
        }

        private final String expression;
        private final boolean isWildcard;
        private CompiledExpression compiledExpression;

        public Filter(String expr) {
            this.expression = expr;
            this.isWildcard = "*".equals(expr);
        }

        public boolean isWildcard() {
            return isWildcard;
        }

        public Filter compile() {
            if (!isWildcard) {
                String expr = expression.replace("%5B", "[").replace("%5D", "]");
                this.compiledExpression = new ExpressionCompiler(expr).compile();
            }
            return this;
        }

        public Boolean apply(Object o) {
            if (isWildcard) {
                return true;
            }

            Map variables = new HashMap();
            if (o instanceof Map) {
                variables = (Map) o;
            }

            if (o instanceof String || o.getClass().isPrimitive() || BOXED_TYPES.contains(o.getClass())) {
                variables = MapBuilder.hashMap().put("$", o).build();
            }

            return MVEL.executeExpression(compiledExpression, variables, Boolean.class);
        }

    }


    /**
     * Return directive for {@link ListQuery}
     */
    public static class Return {

        public enum Type {
            AllMatched,
            FirstMatched,
            LastMatched,
            AtIndex,
            InRange;
        }

        private static final Return ALL_MATCHED = new Return(Type.AllMatched);
        private static final Return FIRST_MATCHED = new Return(Type.FirstMatched);
        private static final Return LAST_MATCHED = new Return(Type.LastMatched);

        private final Type type;

        private int index;

        private int startIndex;
        private int endIndex;

        private Return(Type type) {
            this.type = type;
        }

        public static Return allMatched() {
            return ALL_MATCHED;
        }

        public static Return firstMatched() {
            return FIRST_MATCHED;
        }

        public static Return lastMatched() {
            return LAST_MATCHED;
        }

        public static Return atIndex(int index) {
            return new Return(Type.AtIndex).elementAt(index);
        }

        public static Return inRange(int startIndex, int endIndex) {
            int s = Math.min(startIndex, endIndex);
            int e = Math.max(startIndex, endIndex);
            return new Return(Type.InRange).elementsInRange(s, e);
        }

        private Return elementAt(int index) {
            this.index = index;
            return this;
        }

        private Return elementsInRange(int startIndex, int endIndex) {
            this.startIndex = startIndex;
            this.endIndex = endIndex;
            return this;
        }

        public Type getType() {
            return type;
        }

        public int getIndex() {
            return index;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

    }

    /**
     * List query
     */
    public static class ListQuery {

        private Filter filter;
        private Return returnDirective;

        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }

        public Return getReturnDirective() {
            return returnDirective;
        }

        public void setReturnDirective(Return returnDirective) {
            this.returnDirective = returnDirective;
        }

        public Object on(List list) {
            List matched;

            // wildcard ('*') means all should be matched
            // no need to loop through list and apply the filter
            if (filter.isWildcard()) {
                matched = list;
            } else {
                matched = new ArrayList();
                for (Object e : list) {
                    if (filter.apply(e)) {
                        matched.add(e);
                    }
                }
            }

            if (returnDirective == null) {
                return matched;
            }

            switch (returnDirective.getType()) {
                case FirstMatched:
                    return matched.size() > 0 ? matched.get(0) : null;
                case LastMatched:
                    return matched.size() > 0 ? matched.get(matched.size() - 1) : null;
                case AtIndex: {
                    int index = returnDirective.getIndex();
                    return (index >= 0 && index < matched.size()) ? matched.get(index) : null;
                }
                case InRange: {
                    if (returnDirective.getStartIndex() >= matched.size()) {
                        return new ArrayList<>();
                    }
                    int effectiveEndIndex = Math.min(returnDirective.getEndIndex(), matched.size() - 1);
                    return matched.subList(returnDirective.getStartIndex(), effectiveEndIndex + 1);
                }
                default:
                    return matched;
            }
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Filter filter;
            private Return returnDirective;

            private Builder() {
            }

            public Builder withFilter(Filter filter) {
                this.filter = filter;
                return this;
            }

            public Builder withReturnDirective(Return returnDirective) {
                this.returnDirective = returnDirective;
                return this;
            }

            public ListQuery build() {
                ListQuery q = new ListQuery();
                q.filter = filter;
                q.returnDirective = returnDirective;
                return q;
            }
        }
    }

    /**
     * Path segment
     */
    public static class Segment {

        /**
         * mutually exclusive with 'names'
         */
        private String name;

        /**
         * mutually exclusive with 'name'
         */
        private List<String> names;

        /**
         * Only applicable when the field pointed by this path segment is an Array or List.
         * In other words, it implies this segment points to a field of Array or List
         */
        private ListQuery listQuery;

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

        public ListQuery getListQuery() {
            return listQuery;
        }

        public void setListQuery(ListQuery listQuery) {
            this.listQuery = listQuery;
        }

        public boolean hasListQuery() {
            return listQuery != null;
        }

        public static Builder builderEitherOr(String name, List<String> names) {
            if (name != null) {
                if (names == null || names.isEmpty()) {
                    return new Builder(name);
                }
            } else {
                if (names != null && names.size() > 0) {
                    return new Builder(names);
                }
            }
            throw new IllegalArgumentException("'name' or 'names' cannot both be null or both have values");
        }

        public static Builder builder(String name) {
            return new Builder(name);
        }

        public static Builder builder(List<String> names) {
            return new Builder(names);
        }

        public static class Builder {

            private final String name;
            private final List<String> names;
            private ListQuery listQuery;

            private Builder(String name) {
                this.name = name;
                this.names = null;
            }

            public Builder(List<String> names) {
                this.name = null;
                this.names = names;
            }

            public Builder withListQuery(ListQuery listQuery) {
                this.listQuery = listQuery;
                return this;
            }

            public Segment build() {
                Segment s = new Segment();
                s.name = name;
                s.names = names;
                s.listQuery = listQuery;
                return s;
            }

        }
    }

    private static class TextBasedSegmentBuilder {

        private static final Pattern COMMA_SPLITTER = Pattern.compile(",");

        private StringBuilder nameExprBuilder = new StringBuilder();
        private StringBuilder listQueryExprBuilder = new StringBuilder();

        public TextBasedSegmentBuilder appendToNameExpr(char c) {
            nameExprBuilder.append(c);
            return this;
        }

        public TextBasedSegmentBuilder appendToListQueryExpr(char c) {
            listQueryExprBuilder.append(c);
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

            ListQuery listQuery = null;
            String listQueryExpr = listQueryExprBuilder.toString().trim();
            if (listQueryExpr.length() > 0) {
                Filter filter;
                Return returnDirective;

                if ("*".equals(listQueryExpr)) {
                    filter = new Filter("*");
                    returnDirective = Return.allMatched();
                } else if (listQueryExpr.charAt(0) == '(') {
                    // expected formats
                    //  (filter condition expression)|S
                    //  (filter condition expression)|S:E

                    // find the position of right most right parenthesis
                    int prmrp = listQueryExpr.lastIndexOf(')');
                    if (prmrp < 0) {
                        throw new IllegalArgumentException(String.format("List query expression '%s' is invalid", listQueryExpr));
                    }
                    filter = new Filter(listQueryExpr.substring(0, prmrp + 1));

                    if (prmrp < listQueryExpr.length() - 1) {
                        String remainingExpr = listQueryExpr.substring(prmrp + 1).trim();
                        if (remainingExpr.charAt(0) != '|') {
                            throw new IllegalArgumentException(String.format("List query expression '%s' is invalid", listQueryExpr));
                        }
                        String returnExpr = remainingExpr.substring(1);
                        returnDirective = parseReturnDirective(returnExpr);
                    } else {
                        returnDirective = Return.allMatched();
                    }
                } else {
                    // list query format: [index] or [S:E]
                    filter = new Filter("*");
                    returnDirective = parseReturnDirective(listQueryExpr);
                }

                listQuery = ListQuery.builder().withFilter(filter.compile()).withReturnDirective(returnDirective).build();
            }

            return Segment.builderEitherOr(name, names).withListQuery(listQuery).build();
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

    private static class TextualQueryParser {

        public MapQuery parse(String pathQuery) {
            List<Segment> segments = parseSegments(pathQuery);
            return new MapQuery().withSegments(segments);
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
                throw new IllegalArgumentException(String.format("Path query '%s' is invalid because of malformed list query", path));
            }

            Segment segment = segmentBuilder.build();
            if (segment != null) {
                segments.add(segment);
            }

            return segments;

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
