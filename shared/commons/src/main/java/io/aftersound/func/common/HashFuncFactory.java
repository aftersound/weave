package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.util.TreeNode;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class HashFuncFactory extends MasterAwareFuncFactory {

        private static final List<Descriptor> DESCRIPTORS = Collections.singletonList(
                Descriptor.builder("HASH")
                        .build()
        );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("HASH".equals(funcName)) {
            return createHashFunc(spec);
        }

        return null;
    }

    static abstract class HashFunc<S, T> extends AbstractFuncWithHints<S, T> {

        private final String algorithm;

        private transient MessageDigest md;

        protected HashFunc(String algorithm) {
            this.algorithm = algorithm;
        }

        protected MessageDigest messageDigest() {
            if (md == null) {
                try {
                    md = MessageDigest.getInstance(algorithm);
                } catch (NoSuchAlgorithmException e) {
                    final String msg = String.format("'%s' is not supported", algorithm);
                    throw new ExecutionException(msg, e);
                }
            }
            return md;
        }
    }

    static class FromStringToByteArrayHashFunc extends HashFunc<String, byte[]> {

        public FromStringToByteArrayHashFunc(final String algorithm) {
            super(algorithm);
        }

        @Override
        public byte[] apply(String source) {
            if (source != null) {
                return messageDigest().digest(source.getBytes(StandardCharsets.UTF_8));
            } else {
                return null;
            }
        }

    }

    static class FromStringToStringHashFunc extends HashFunc<String, String> {

        public FromStringToStringHashFunc(final String algorithm) {
            super(algorithm);
        }

        @Override
        public String apply(String source) {
            if (source != null) {
                return HexFuncFactory.HexStringFunc.INSTANCE.apply(messageDigest().digest(source.getBytes(StandardCharsets.UTF_8)));
            } else {
                return null;
            }
        }

    }

    static class FromByteArrayToStringHashFunc extends HashFunc<byte[], String> {

        public FromByteArrayToStringHashFunc(final String algorithm) {
            super(algorithm);
        }

        @Override
        public String apply(byte[] source) {
            if (source != null) {
                return HexFuncFactory.HexStringFunc.INSTANCE.apply(messageDigest().digest(source));
            } else {
                return null;
            }
        }

    }

    static class FromByteArrayToByteArrayHashFunc extends HashFunc<byte[], byte[]> {

        public FromByteArrayToByteArrayHashFunc(final String algorithm) {
            super(algorithm);
        }

        @Override
        public byte[] apply(byte[] source) {
            if (source != null) {
                return messageDigest().digest(source);
            } else {
                return null;
            }
        }

    }

    private Func createHashFunc(TreeNode spec) {
        final String algorithm = spec.getDataOfChildAt(0);
        final String sourceType = spec.getDataOfChildAt(1, "ByteArray");
        final String targetType = spec.getDataOfChildAt(2, "ByteArray");

        try {
            if (algorithm == null || algorithm.isEmpty()) {
                throw new Exception("algorithm is null");
            }

            if ("String".equals(sourceType)) {
                if ("String".equals(targetType)) {
                    return new FromStringToStringHashFunc(algorithm);
                } else if ("ByteArray".equals(targetType)){
                    return new FromStringToByteArrayHashFunc(algorithm);
                }
            } else if ("ByteArray".equals(sourceType)) {
                if ("String".equals(targetType)) {
                    return new FromByteArrayToStringHashFunc(algorithm);
                } else if ("ByteArray".equals(targetType)) {
                    return new FromByteArrayToByteArrayHashFunc(algorithm);
                }
            }

            throw new Exception(String.format("sourceType as '%s' is not supported", sourceType));
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "HASH(algorithm,sourceType,targetType)",
                    "HASH(SHA-256) or HASH(SHA-256,String,String)"
            );
        }
    }

}
