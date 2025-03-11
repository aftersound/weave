package io.aftersound.func.common;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.util.TreeNode;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class Base64FuncFactory extends MasterAwareFuncFactory {

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("BASE64:DECODE".equals(funcName)) {
            return createDecodeFunc(spec);
        }

        if ("BASE64:ENCODE".equals(funcName)) {
            return createEncodeFunc(spec);
        }

        return null;
    }

    static class FromStringToStringEncodeFunc extends AbstractFuncWithHints<String, String> {

        @Override
        public String apply(String source) {
            if (source == null || source.isEmpty()) {
                return source;
            }

            byte[] encoded = Base64.getEncoder().encode(source.getBytes(StandardCharsets.UTF_8));
            return new String(encoded);
        }

    }

    static class FromStringToByteArrayEncodeFunc extends AbstractFuncWithHints<String, byte[]> {

        @Override
        public byte[] apply(String source) {
            if (source == null) {
                return null;
            }
            if (source.isEmpty()) {
                return new byte[0];
            }

            return Base64.getEncoder().encode(source.getBytes(StandardCharsets.UTF_8));
        }

    }

    static class FromStringToByteBufferEncodeFunc extends AbstractFuncWithHints<String, ByteBuffer> {

        @Override
        public ByteBuffer apply(String source) {
            if (source == null) {
                return null;
            }
            if (source.isEmpty()) {
                return ByteBuffer.wrap(new byte[0]);
            }

            byte[] encoded = Base64.getEncoder().encode(source.getBytes(StandardCharsets.UTF_8));
            return ByteBuffer.wrap(encoded);
        }

    }

    static class FromByteArrayToStringEncodeFunc extends AbstractFuncWithHints<byte[], String> {

        @Override
        public String apply(byte[] source) {
            if (source == null) {
                return null;
            }
            if (source.length == 0) {
                return "";
            }
            byte[] encoded = Base64.getEncoder().encode(source);
            return new String(encoded);
        }

    }

    static class FromByteArrayToByteArrayEncodeFunc extends AbstractFuncWithHints<byte[], byte[]> {

        @Override
        public byte[] apply(byte[] source) {
            if (source == null || source.length == 0) {
                return source;
            }
            return Base64.getEncoder().encode(source);
        }

    }

    static class FromByteArrayToByteBufferEncodeFunc extends AbstractFuncWithHints<byte[], ByteBuffer> {

        @Override
        public ByteBuffer apply(byte[] source) {
            if (source == null) {
                return null;
            }
            if (source.length == 0) {
                return ByteBuffer.wrap(source);
            }
            byte[] encoded = Base64.getEncoder().encode(source);
            return ByteBuffer.wrap(encoded);
        }

    }

    static class FromByteBufferToStringEncodeFunc extends AbstractFuncWithHints<ByteBuffer, String> {

        @Override
        public String apply(ByteBuffer source) {
            if (source == null) {
                return null;
            }
            byte[] bytes = source.array();
            if (bytes.length == 0) {
                return "";
            }
            byte[] encoded = Base64.getEncoder().encode(bytes);
            return new String(encoded);
        }

    }

    static class FromByteBufferToByteArrayEncodeFunc extends AbstractFuncWithHints<ByteBuffer, byte[]> {

        @Override
        public byte[] apply(ByteBuffer source) {
            if (source == null) {
                return null;
            }
            byte[] bytes = source.array();
            if (bytes.length == 0) {
                return new byte[0];
            }
            return Base64.getEncoder().encode(bytes);
        }

    }

    static class FromByteBufferToByteBufferEncodeFunc extends AbstractFuncWithHints<ByteBuffer, ByteBuffer> {

        @Override
        public ByteBuffer apply(ByteBuffer source) {
            if (source == null) {
                return null;
            }
            byte[] bytes = source.array();
            if (bytes.length == 0) {
                return ByteBuffer.wrap(new byte[0]);
            }
            byte[] encoded = Base64.getEncoder().encode(bytes);
            return ByteBuffer.wrap(encoded);
        }

    }

    static class FromStringToStringDecodeFunc extends AbstractFuncWithHints<String, String> {

        @Override
        public String apply(String source) {
            if (source == null) {
                return null;
            }

            byte[] decoded = Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8));
            return new String(decoded);
        }
    }

    static class FromStringToByteArrayDecodeFunc extends AbstractFuncWithHints<String, byte[]> {

        @Override
        public byte[] apply(String source) {
            if (source == null) {
                return null;
            }
            if (source.isEmpty()) {
                return new byte[0];
            }

            return Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8));
        }
    }

    static class FromStringToByteBufferDecodeFunc extends AbstractFuncWithHints<String, ByteBuffer> {

        @Override
        public ByteBuffer apply(String source) {
            if (source == null) {
                return null;
            }
            if (source.isEmpty()) {
                return ByteBuffer.wrap(new byte[0]);
            }

            byte[] decoded = Base64.getDecoder().decode(source.getBytes(StandardCharsets.UTF_8));
            return ByteBuffer.wrap(decoded);
        }

    }

    static class FromByteArrayToStringDecodeFunc extends AbstractFuncWithHints<byte[], String> {

        @Override
        public String apply(byte[] source) {
            if (source == null) {
                return null;
            }
            if (source.length == 0) {
                return "";
            }
            byte[] decoded = Base64.getDecoder().decode(source);
            return new String(decoded);
        }

    }

    static class FromByteArrayToByteArrayDecodeFunc extends AbstractFuncWithHints<byte[], byte[]> {

        @Override
        public byte[] apply(byte[] source) {
            if (source == null || source.length == 0) {
                return source;
            }
            return Base64.getDecoder().decode(source);
        }

    }

    static class FromByteArrayToByteBufferDecodeFunc extends AbstractFuncWithHints<byte[], ByteBuffer> {

        @Override
        public ByteBuffer apply(byte[] source) {
            if (source == null) {
                return null;
            }
            if (source.length == 0) {
                return ByteBuffer.wrap(source);
            }
            byte[] decoded = Base64.getDecoder().decode(source);
            return ByteBuffer.wrap(decoded);
        }

    }

    static class FromByteBufferToStringDecodeFunc extends AbstractFuncWithHints<ByteBuffer, String> {

        @Override
        public String apply(ByteBuffer source) {
            if (source == null) {
                return null;
            }
            byte[] bytes = source.array();
            if (bytes.length == 0) {
                return "";
            }
            byte[] decoded = Base64.getDecoder().decode(bytes);
            return new String(decoded);
        }

    }

    static class FromByteBufferToByteArrayDecodeFunc extends AbstractFuncWithHints<ByteBuffer, byte[]> {

        @Override
        public byte[] apply(ByteBuffer source) {
            if (source == null) {
                return null;
            }
            byte[] bytes = source.array();
            if (bytes.length == 0) {
                return new byte[0];
            }
            return Base64.getDecoder().decode(bytes);
        }

    }

    static class FromByteBufferToByteBufferDecodeFunc extends AbstractFuncWithHints<ByteBuffer, ByteBuffer> {

        @Override
        public ByteBuffer apply(ByteBuffer source) {
            if (source == null) {
                return null;
            }
            byte[] bytes = source.array();
            if (bytes.length == 0) {
                return ByteBuffer.wrap(new byte[0]);
            }
            byte[] decoded = Base64.getDecoder().decode(bytes);
            return ByteBuffer.wrap(decoded);
        }

    }

    private Func createEncodeFunc(TreeNode spec) {
        final String sourceType = spec.getDataOfChildAt(0, "Bytes");
        final String targetType = spec.getDataOfChildAt(1, "Bytes");

        if ("String".equals(sourceType)) {
            if ("String".equals(targetType)) {
                return new FromStringToStringEncodeFunc();
            }

            if ("Bytes".equals(targetType) || "ByteArray".equals(targetType)) {
                return new FromStringToByteArrayEncodeFunc();
            }

            if ("ByteBuffer".equals(targetType)) {
                return new FromStringToByteBufferEncodeFunc();
            }
        }

        if ("Bytes".equals(sourceType) || "ByteArray".equals(sourceType)) {
            if ("String".equals(targetType)) {
                return new FromByteArrayToStringEncodeFunc();
            }

            if ("Bytes".equals(targetType) || "ByteArray".equals(targetType)) {
                return new FromByteArrayToByteArrayEncodeFunc();
            }

            if ("ByteBuffer".equals(targetType)) {
                return new FromByteArrayToByteBufferEncodeFunc();
            }
        }

        if ("ByteBuffer".equals(sourceType)) {
            if ("String".equals(targetType)) {
                return new FromByteBufferToStringEncodeFunc();
            }

            if ("Bytes".equals(targetType) || "ByteArray".equals(targetType)) {
                return new FromByteBufferToByteArrayEncodeFunc();
            }

            if ("ByteBuffer".equals(targetType)) {
                return new FromByteBufferToByteBufferEncodeFunc();
            }
        }

        throw new CreationException(spec + " is not supported");
    }

    private Func createDecodeFunc(TreeNode spec) {
        final String sourceType = spec.getDataOfChildAt(0, "Bytes");
        final String targetType = spec.getDataOfChildAt(1, "Bytes");

        if ("String".equals(sourceType)) {
            if ("String".equals(targetType)) {
                return new FromStringToStringDecodeFunc();
            }

            if ("Bytes".equals(targetType) || "ByteArray".equals(targetType)) {
                return new FromStringToByteArrayDecodeFunc();
            }

            if ("ByteBuffer".equals(targetType)) {
                return new FromStringToByteBufferDecodeFunc();
            }
        }

        if ("Bytes".equals(sourceType) || "ByteArray".equals(sourceType)) {
            if ("String".equals(targetType)) {
                return new FromByteArrayToStringDecodeFunc();
            }

            if ("Bytes".equals(targetType) || "ByteArray".equals(targetType)) {
                return new FromByteArrayToByteArrayDecodeFunc();
            }

            if ("ByteBuffer".equals(targetType)) {
                return new FromByteArrayToByteBufferDecodeFunc();
            }
        }

        if ("ByteBuffer".equals(sourceType)) {
            if ("String".equals(targetType)) {
                return new FromByteBufferToStringDecodeFunc();
            }

            if ("Bytes".equals(targetType) || "ByteArray".equals(targetType)) {
                return new FromByteBufferToByteArrayDecodeFunc();
            }

            if ("ByteBuffer".equals(targetType)) {
                return new FromByteBufferToByteBufferDecodeFunc();
            }
        }

        throw new CreationException(spec + " is not supported");
    }

}
