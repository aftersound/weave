package io.aftersound.func.common;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.Descriptor;
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

@SuppressWarnings({ "rawtypes", "unchecked" })
public class StringFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor.builder("STRING", "TBD", "TBD")
//                    .withAliases("STR")
//                    .build(),
//            Descriptor.builder("STRING:CONTAINS", "TBD", "TBD")
//                    .withAliases("STR:CONTAINS")
//                    .build(),
//            Descriptor.builder("STRING:DECODE", "TBD", "TBD")
//                    .withAliases("STR:DECODE")
//                    .build(),
//            Descriptor.builder("STRING:ENCODE", "TBD", "TBD")
//                    .withAliases("STR:ENCODE")
//                    .build(),
//            Descriptor.builder("STRING:END_WITH", "TBD", "TBD")
//                    .withAliases("STR:END_WITH")
//                    .build(),
//            Descriptor.builder("STRING:FROM", "TBD", "TBD")
//                    .withAliases("STR:FROM")
//                    .build(),
//            Descriptor.builder("STRING:JOIN", "TBD", "TBD")
//                    .withAliases("STR:JOIN")
//                    .build(),
//            Descriptor.builder("STRING:MATCH", "TBD", "TBD")
//                    .withAliases("STR:MATCH")
//                    .build(),
//            Descriptor.builder("STRING:RANDOM", "TBD", "TBD")
//                    .withAliases("STR:RANDOM")
//                    .build(),
//            Descriptor.builder("STRING:READ_LINES", "TBD", "TBD")
//                    .withAliases("STR:READ_LINES")
//                    .build(),
//            Descriptor.builder("STRING:SPLIT", "TBD", "TBD")
//                    .withAliases("STR:SPLIT")
//                    .build(),
//            Descriptor.builder("STRING:START_WITH", "TBD", "TBD")
//                    .withAliases("STR:START_WITH")
//                    .build(),
//            Descriptor.builder("STRING:LIST:FROM", "TBD", "TBD")
//                    .withAliases("STR:LIST:FROM")
//                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        switch (funcName) {
            case "STR":
            case "STRING": {
                return createLiteralFunc(spec);
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
            case "STR:LIST:FROM":
            case "STRING:LIST:FROM": {
                return createListFromFunc(spec);
            }
            default: {
                return null;
            }
        }
    }

    static class StringContainsFunc extends AbstractFuncWithHints<String, Boolean> {

        private final String literal;

        public StringContainsFunc(String literal) {
            this.literal = literal;
        }

        @Override
        public Boolean apply(String source) {
            return source != null && source.contains(literal) ? Boolean.TRUE : Boolean.FALSE;
        }
    }

    static class StringJoinFunc extends AbstractFuncWithHints<Object, String> {

        private final List<Func<Object, String>> valueFuncList;
        private final String delimiter;

        public StringJoinFunc(List<Func<Object, String>> valueFuncList, String delimiter) {
            this.valueFuncList = valueFuncList;
            this.delimiter = delimiter;
        }

        @Override
        public String apply(Object source) {
            if (source != null) {
                StringJoiner joiner = new StringJoiner(delimiter);
                for (Func<Object, String> valueFunc : valueFuncList) {
                    Object v = valueFunc.apply(source);
                    joiner.add(v != null ? v.toString() : null);
                }
                return joiner.toString();
            }
            return null;
        }

    }

    static class StringSplitFunc extends AbstractFuncWithHints<String, List<String>> {

        private final Pattern pattern;

        public StringSplitFunc(String regex) {
            this.pattern = Pattern.compile(regex);
        }

        @Override
        public List<String> apply(String source) {
            return source != null ? Arrays.asList(pattern.split(source)) : null;
        }

    }

    static class StringFromFunc extends AbstractFuncWithHints<Object, String> {

        @Override
        public String apply(Object source) {
            return source != null ? source.toString() : null;
        }

    }

    static class StringStartWithFunc extends AbstractFuncWithHints<String, Boolean> {

        private final String prefix;

        public StringStartWithFunc(String prefix) {
            this.prefix = prefix;
        }
        @Override
        public Boolean apply(String source) {
            return source != null ? source.startsWith(prefix) : null;
        }

    }

    static class StringEndWithFunc extends AbstractFuncWithHints<String, Boolean> {

        private final String suffix;

        public StringEndWithFunc(String prefix) {
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

    static class StringMatchFunc extends AbstractFuncWithHints<String, Boolean> {

        private final Pattern pattern;

        public StringMatchFunc(String pattern) {
            this.pattern = Pattern.compile(pattern);
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

    private Func createJoinFunc(TreeNode spec) {
        final int childrenCount = spec.getChildrenCount();

        // list of sub AbstractFuncWithHints<Object,String>
        final List<TreeNode> subValueSpecList = spec.getChildren(0, childrenCount - 2);
        List<Func<Object, String>> valueFuncList = new ArrayList<>(subValueSpecList.size());
        for (TreeNode subValueSpec : subValueSpecList) {
            valueFuncList.add(masterFuncFactory.create(subValueSpec));
        }

        // delimiter
        String str = spec.getDataOfChildAt(childrenCount - 1);
        final String delimiter;
        if (str != null && !str.isEmpty()) {
            delimiter = normalize(str);
        } else {
            delimiter = "";
        }

        return new StringJoinFunc(valueFuncList, delimiter);
    }

    private Func createSplitFunc(TreeNode spec) {
        String v = spec.getDataOfChildAt(0);
        String regex = normalize(v);
        return new StringSplitFunc(regex);
    }

    private Func createFromFunc(TreeNode spec) {
        return new StringFromFunc();
    }

    private Func createLiteralFunc(TreeNode spec) {
        // STR(literal) or STRING(literal)
        final String literal = spec.getDataOfChildAt(0);
        return masterFuncFactory.create(String.format("VAL(String,%s)", literal));
    }

    private Func createContainsFunc(TreeNode spec) {
        // STR:CONTAINS(literal) or STRING:CONTAINS(literal)
        final String literal = spec.getDataOfChildAt(0);
        return new StringContainsFunc(literal);
    }

    private Func createStartWithFunc(TreeNode spec) {
        final String prefix = normalize(spec.getDataOfChildAt(0));
        return new StringStartWithFunc(prefix);
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
        return new StringEndWithFunc(suffix);
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
        final String pattern = normalize(spec.getDataOfChildAt(0));
        return new StringMatchFunc(pattern);
    }

    private Func createReadLinesFunc(TreeNode spec) {
        return new ReadLinesFunc();
    }

    static AbstractFuncWithHints createListFromFunc(TreeNode spec) {
        return new FromList();
    }

}
