package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.Constraint;
import io.aftersound.schema.Field;
import io.aftersound.util.TreeNode;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class URLFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor
                    .builder("URL:DECODE")
                    .withDescription("Decode an application/x-www-form-urlencoded string using a specific encoding scheme")
                    .withControls(
                            Field.stringFieldBuilder("charsetName")
                                    .withConstraint(Constraint.optional())
                                    .withDescription("The character encoding used for the URL decoding. When missing, default is UTF-8.")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "URL:DECODE(UTF-8)",
                                    "Decodes an application/x-www-form-urlencoded string using UTF-8 character set"
                            )
                    )
                    .build(),
            Descriptor
                    .builder("URL:ENCODE")
                    .withDescription("Encode input String into application/x-www-form-urlencoded string using a specific encoding scheme")
                    .withControls(
                            Field.stringFieldBuilder("charsetName")
                                    .withDescription("The character encoding used for the URL encoding. When missing, default is UTF-8.")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "URL:ENCODE(UTF-8)",
                                    "URL encode input String using UTF-8 character set"
                            )
                    )
                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("URL:DECODE".equals(funcName)) {
            return createDecodeFunc(spec);
        }

        if ("URL:ENCODE".equals(funcName)) {
            return createEncodeFunc(spec);
        }

        return null;
    }

    static class URLDecodeFunc extends AbstractFuncWithHints<String, String> {

        public static final URLDecodeFunc INSTANCE = new URLDecodeFunc("UTF-8");

        private final String charset;

        private URLDecodeFunc(String charset) {
            this.charset = charset;
        }

        @Override
        public String apply(String source) {
            if (source != null) {
                try {
                    return URLDecoder.decode(source, charset);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            } else {
                return null;
            }
        }

    }

    static class URLEncodeFunc extends AbstractFuncWithHints<String, String> {

        public static final URLEncodeFunc INSTANCE = new URLEncodeFunc("UTF-8");

        private final String charset;

        private URLEncodeFunc(String charset) {
            this.charset = charset;
        }

        @Override
        public String apply(String source) {
            if (source != null) {
                try {
                    return URLEncoder.encode(source, charset);
                } catch (UnsupportedEncodingException e) {
                    return null;
                }
            } else {
                return null;
            }
        }

    }

    private Func createDecodeFunc(TreeNode spec) {
        final String charset = spec.getDataOfChildAt(0, "UTF-8");
        return new URLDecodeFunc(charset);
    }

    private Func createEncodeFunc(TreeNode spec) {
        final String charset = spec.getDataOfChildAt(0, "UTF-8");
        return new URLEncodeFunc(charset);
    }

}
