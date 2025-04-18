package io.aftersound.func.common;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.Func;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.util.TreeNode;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static io.aftersound.func.FuncHelper.createCreationException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StringFuncFactory extends MasterAwareFuncFactory {

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        switch (funcName) {
            case "LIST<STR>:":
            case "LIST<STRING>:": {
                return createListFunc(spec);
            }
            case "LIST<STR>:FROM":
            case "LIST<STRING>:FROM": {
                return createListFromFunc(spec);
            }
            case "SET<STR>":
            case "SET<STRING>": {
                return createSetFunc(spec);
            }
            case "STR":
            case "STRING": {
                return createLiteralFunc(spec);
            }
            case "STR:ANY_OF":
            case "STRING:ANY_OF": {
                return createAnyOfFunc(spec);
            }
            case "STR:CONTAINS":
            case "STRING:CONTAINS": {
                return createContainsFunc(spec);
            }
            case "STR:DECODE":
            case "STRING:DECODE": {
                return createDecodeFunc(spec);
            }
            case "STR:ENCODE":
            case "STRING:ENCODE": {
                return createEncodeFunc(spec);
            }
            case "STR:END_WITH":
            case "STRING:END_WITH": {
                return createEndWithFunc(spec);
            }
            case "STR:FROM":
            case "STRING:FROM": {
                return createFromFunc(spec);
            }
            case "STR:JOIN":
            case "STRING:JOIN": {
                return createJoinFunc(spec);
            }
            case "STR:LENGTH":
            case "STRING:LENGTH": {
                return createLengthFunc(spec);
            }
            case "STR:LENGTH_WITHIN":
            case "STRING:LENGTH_WITHIN": {
                return createLengthWithinFunc(spec);
            }
            case "STR:MATCH":
            case "STRING:MATCH": {
                return createMatchFunc(spec);
            }
            case "STR:RANDOM":
            case "STRING:RANDOM": {
                return createRandomStringFunc(spec);
            }
            case "STR:READ_LINES":
            case "STRING:READ_LINES": {
                return createReadLinesFunc(spec);
            }
            case "STR:SPLIT":
            case "STRING:SPLIT": {
                return createSplitFunc(spec);
            }
            case "STR:START_WITH":
            case "STRING:START_WITH": {
                return createStartWithFunc(spec);
            }
            default: {
                return null;
            }
        }
    }

    static class ListFunc extends AbstractFuncWithHints<Object, List<String>> {

        private final List<String> list;

        public ListFunc(List<String> list) {
            this.list = list;
        }

        @Override
        public List<String> apply(Object o) {
            return new ArrayList<>(list);
        }

    }

    static class SetFunc extends AbstractFuncWithHints<Object, Set<String>> {

        private final Set<String> set;

        public SetFunc(Set<String> set) {
            this.set = set;
        }

        @Override
        public Set<String> apply(Object o) {
            return new LinkedHashSet<>(set);
        }

    }

    static class LiteralFunc extends AbstractFuncWithHints<Object, String> {

        private final String literal;

        public LiteralFunc(String literal) {
            this.literal = literal;
        }

        @Override
        public String apply(Object source) {
            return literal;
        }
    }

    static class AnyOfFunc extends AbstractFuncWithHints<String, Boolean> {

        private final Set<String> set;

        public AnyOfFunc(Set<String> set) {
            this.set = set;
        }

        @Override
        public Boolean apply(String source) {
            return source != null && set.contains(source);
        }
    }

    static class ContainsFunc extends AbstractFuncWithHints<String, Boolean> {

        private final String literal;

        public ContainsFunc(String literal) {
            this.literal = literal;
        }

        @Override
        public Boolean apply(String source) {
            return source != null && source.contains(literal) ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    static class JoinFunc extends AbstractFuncWithHints<List<String>, String> {

        private final String delimiter;

        public JoinFunc(String delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public String apply(List<String> source) {
            if (source != null) {
                StringJoiner joiner = new StringJoiner(delimiter);
                for (String s : source) {
                    joiner.add(s);
                }
                return joiner.toString();
            }
            return null;
        }

    }

    static class LengthFunc extends AbstractFuncWithHints<String, Integer> {

        @Override
        public Integer apply(String str) {
            return str != null ? str.length() : 0;
        }

    }

    static class LengthWithinFunc extends AbstractFuncWithHints<String, Boolean> {

        private final int lowerBound;
        private final int upperBound;
        private final boolean lowerInclusive;
        private final boolean upperInclusive;

        public LengthWithinFunc(int lowerBound, int upperBound, boolean lowerInclusive, boolean upperInclusive) {
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.lowerInclusive = lowerInclusive;
            this.upperInclusive = upperInclusive;
        }

        @Override
        public Boolean apply(String str) {
            if (str != null) {
                int value = str.length();
                return  ((lowerInclusive && value >= lowerBound) || (!lowerInclusive && value > lowerBound)) &&
                        ((upperInclusive && value <= upperBound) || (!upperInclusive && value < upperBound));
            } else {
                return false;
            }
        }

    }

    static class SplitFunc extends AbstractFuncWithHints<String, List<String>> {

        private final Pattern pattern;

        public SplitFunc(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public List<String> apply(String source) {
            return source != null ? Arrays.asList(pattern.split(source)) : null;
        }

    }

    static class FromFunc extends AbstractFuncWithHints<Object, String> {

        @Override
        public String apply(Object source) {
            return source != null ? source.toString() : null;
        }

    }

    static class StartWithFunc extends AbstractFuncWithHints<String, Boolean> {

        private final String prefix;

        public StartWithFunc(String prefix) {
            this.prefix = prefix;
        }
        @Override
        public Boolean apply(String source) {
            return source != null ? source.startsWith(prefix) : null;
        }

    }

    static class EndWithFunc extends AbstractFuncWithHints<String, Boolean> {

        private final String suffix;

        public EndWithFunc(String prefix) {
            this.suffix = prefix;
        }
        @Override
        public Boolean apply(String source) {
            return source != null ? source.endsWith(suffix) : null;
        }

    }

    static class DecodeFunc extends AbstractFuncWithHints<byte[], String> {

        private final String charsetName;

        private transient Charset charset;

        public DecodeFunc(String charsetName) {
            this.charsetName = charsetName;
            try {
                charset = Charset.forName(charsetName);
            } catch (Exception e) {
                charset = Charset.defaultCharset();
            }
        }

        @Override
        public String apply(byte[] source) {
            try {
                charset = Charset.forName(charsetName);
            } catch (Exception e) {
                charset = Charset.defaultCharset();
            }
            return source != null ? new String(source, charset) : null;
        }

    }

    static class EncodeFunc extends AbstractFuncWithHints<String, byte[]> {

        private final String charsetName;

        private transient Charset charset;

        public EncodeFunc(String charsetName) {
            this.charsetName = charsetName;
            try {
                charset = Charset.forName(charsetName);
            } catch (Exception e) {
                charset = Charset.defaultCharset();
            }
        }

        @Override
        public byte[] apply(String source) {
            if (charset == null) {
                try {
                    charset = Charset.forName(charsetName);
                } catch (Exception e) {
                    charset = Charset.defaultCharset();
                }
            }
            return source != null ? source.getBytes(charset) : null;
        }

    }

    static class ReadLinesFunc extends AbstractFuncWithHints<String, List<String>> {

        @Override
        public List<String> apply(String source) {
            if (source != null) {
                try (BufferedReader reader = new BufferedReader(new StringReader(source))) {
                    List<String> lines = new ArrayList<>();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }
                    return lines;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return null;
            }
        }

    }

    static class RandomStringFunc extends AbstractFuncWithHints<Object, String> {

        private final int max;
        private final ThreadLocalRandom random;

        public RandomStringFunc(int max) {
            this.max = max;
            this.random = ThreadLocalRandom.current();
        }

        @Override
        public String apply(Object source) {
            final int length = random.nextInt(max) + 1;
            char[] chars = new char[length];
            for (int i = 0; i < length; i++) {
                final int letterIndex = random.nextInt(26) + 1;
                chars[i] = (char) (96 + letterIndex);
            }
            return new String(chars);
        }
    }

    static class MatchFunc extends AbstractFuncWithHints<String, Boolean> {

        private final Pattern pattern;

        public MatchFunc(Pattern pattern) {
            this.pattern = pattern;
        }

        @Override
        public Boolean apply(String source) {
            return source != null && pattern.matcher(source).matches();
        }
    }

    static class FromList<S> extends AbstractFuncWithHints<List<S>, List<String>> {

        @Override
        public final List<String> apply(List<S> source) {
            if (source != null) {
                List<String> values = new ArrayList<>(source.size());
                for (S e : source) {
                    values.add(e != null ? e.toString() : null);
                }
                return values;
            }
            return null;
        }

    }

    private static String normalize(String value) {
        if (value != null) {
            if (value.startsWith("BASE64|")) {
                value = value.substring("BASE64|".length());
                return new String(Base64.getDecoder().decode(value));
            }
            if (value.startsWith("URL|")) {
                value = value.substring("URL|".length());
                return URLDecoder.decode(value, StandardCharsets.UTF_8);
            }
            return value;
        }
        return value;
    }

    private Func createAnyOfFunc(TreeNode spec) {
        try {
            List<String> list = spec.getDataOfChildren();
            Set<String> set = new HashSet<>(list);
            return new AnyOfFunc(set);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "STRING:ANY_OF(literal0,literal1,...)",
                    "STRING:ANY_OF(a,b,c)"
            );
        }
    }

    private Func createListFunc(TreeNode spec) {
        try {
            List<String> list = spec.getDataOfChildren();
            return new ListFunc(list);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "LIST<STRING>(l1,l2,...) or LIST<STR>(l1,l2,...)",
                    "LIST<STRING>(a,b,c)"
            );
        }
    }

    private Func createSetFunc(TreeNode spec) {
        try {
            List<String> list = spec.getDataOfChildren();
            return new SetFunc(new LinkedHashSet<>(list));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "SET<STRING>(l1,l2,...) or SET<STR>(l1,l2,...)",
                    "SET<STRING>(a,b,c)"
            );
        }
    }

    private Func createJoinFunc(TreeNode spec) {
        String str = spec.getDataOfChildAt(0);
        final String delimiter;
        if (str != null && !str.isEmpty()) {
            delimiter = normalize(str);
        } else {
            delimiter = "";
        }

        return new JoinFunc(delimiter);
    }

    private Func createSplitFunc(TreeNode spec) {
        String v = spec.getDataOfChildAt(0);
        try {
            String regex = normalize(v);
            Pattern pattern = Pattern.compile(regex);
            return new SplitFunc(pattern);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "STRING:SPLIT(regex) or STRING:SPLIT(BASE64|encodedRegex) or STRING:SPLIT(URL|encodedRegex)",
                    "STRING:SPLIT(abc)"
            );
        }
    }

    private Func createFromFunc(TreeNode spec) {
        return new FromFunc();
    }

    private Func createLiteralFunc(TreeNode spec) {
        // STR(literal) or STRING(literal)
        final String literal = spec.getDataOfChildAt(0);
        return new LiteralFunc(literal);
    }

    private Func createContainsFunc(TreeNode spec) {
        // STR:CONTAINS(literal) or STRING:CONTAINS(literal)
        final String literal = spec.getDataOfChildAt(0);
        return new ContainsFunc(literal);
    }

    private Func createLengthFunc(TreeNode spec) {
        return new LengthFunc();
    }

    private Func createLengthWithinFunc(TreeNode spec) {
        final String l = spec.getDataOfChildAt(0);
        final String u = spec.getDataOfChildAt(1);
        final String li = spec.getDataOfChildAt(2, "I");
        final String ui = spec.getDataOfChildAt(3, "I");

        try {
            int lowerBound = Integer.parseInt(l);
            int upperBound = Integer.parseInt(u);

            if (!("I".equalsIgnoreCase(li) || "E".equalsIgnoreCase(li))) {
                throw new Exception("Only [I,i,E,e] are supported inclusive/exclusive indicator");
            }
            boolean lowerInclusive = "I".equalsIgnoreCase(li);

            if (!("I".equalsIgnoreCase(ui) || "E".equalsIgnoreCase(ui))) {
                throw new Exception("Only [I,i,E,e] are supported inclusive/exclusive indicator");
            }
            boolean upperInclusive = "I".equalsIgnoreCase(ui);

            return new LengthWithinFunc(lowerBound, upperBound, lowerInclusive, upperInclusive);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "STRING:LENGTH_WITHIN(lowerBound,upperBound)",
                    "STRING:LENGTH_WITHIN(1,100)"
            );
        }
    }

    private Func createStartWithFunc(TreeNode spec) {
        final String prefix = normalize(spec.getDataOfChildAt(0));
        return new StartWithFunc(prefix);
    }

    private Func createDecodeFunc(TreeNode spec) {
        final String charsetName = spec.getDataOfChildAt(0, StandardCharsets.UTF_8.name());
        return new DecodeFunc(charsetName);
    }

    private Func createEncodeFunc(TreeNode spec) {
        final String charsetName = spec.getDataOfChildAt(0, StandardCharsets.UTF_8.name());
        return new EncodeFunc(charsetName);
    }

    private Func createEndWithFunc(TreeNode spec) {
        final String suffix = normalize(spec.getDataOfChildAt(0));
        return new EndWithFunc(suffix);
    }

    private Func createRandomStringFunc(TreeNode spec) {
        final String s = spec.getDataOfChildAt(0);
        int max = 10;
        if (s != null) {
            try {
                max = Integer.parseInt(s);
            } catch (Exception ignored) {
            }
            if (max <= 0) {
                max = 1;
            }
        }
        return new RandomStringFunc(max);
    }

    private Func createMatchFunc(TreeNode spec) {
        try {
            final String pattern = normalize(spec.getDataOfChildAt(0));
            Pattern p = Pattern.compile(pattern);
            return new MatchFunc(p);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "STRING:MATCH(regex) or STRING:MATCH(BASE64|encodedRegex) or STRING:MATCH(URL|encodedRegex)",
                    "STRING:MATCH(abc)"
            );
        }
    }

    private Func createReadLinesFunc(TreeNode spec) {
        return new ReadLinesFunc();
    }

    static AbstractFuncWithHints createListFromFunc(TreeNode spec) {
        return new FromList();
    }

}
