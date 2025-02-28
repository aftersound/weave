package io.aftersound.func.common;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.Descriptor;
import io.aftersound.func.Func;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.util.TreeNode;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class HexFuncFactory extends MasterAwareFuncFactory {

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("HEX:PARSE")
                            .build(),
            Descriptor.builder("HEX:STRING")
                    .build()
    );


    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("HEX:PARSE".equals(funcName)) {
            return createHexParseFunc(spec);
        }

        if ("HEX:STRING".equals(funcName)) {
            return createHexStringFunc(spec);
        }

        return null;
    }

    static class HexStringFunc extends AbstractFuncWithHints<byte[], String> {

        public static final HexStringFunc INSTANCE = new HexStringFunc();

        @Override
        public String apply(byte[] source) {
            if (source != null) {
                StringBuilder r = new StringBuilder(source.length * 2);
                for (byte b : source) {
                    r.append(hexCode[(b >> 4) & 0xF]);
                    r.append(hexCode[(b & 0xF)]);
                }
                return r.toString();
            } else {
                return null;
            }
        }

    }

    static class HexParseFunc extends AbstractFuncWithHints<String, byte[]> {

        public static final HexParseFunc INSTANCE = new HexParseFunc();

        @Override
        public byte[] apply(String source) {
            if (source != null) {
                final int len = source.length();

                // "111" is not a valid hex encoding.
                if (len % 2 != 0) {
                    throw new IllegalArgumentException("hexBinary needs to be even-length: " + source);
                }

                byte[] out = new byte[len / 2];

                for (int i = 0; i < len; i += 2) {
                    int h = hexToBin(source.charAt(i));
                    int l = hexToBin(source.charAt(i + 1));
                    if (h == -1 || l == -1) {
                        throw new IllegalArgumentException("contains illegal character for hexBinary: " + source);
                    }

                    out[i / 2] = (byte) (h * 16 + l);
                }

                return out;
            } else {
                return null;
            }
        }

        private static int hexToBin(char ch) {
            if ('0' <= ch && ch <= '9') {
                return ch - '0';
            }
            if ('A' <= ch && ch <= 'F') {
                return ch - 'A' + 10;
            }
            if ('a' <= ch && ch <= 'f') {
                return ch - 'a' + 10;
            }
            return -1;
        }

    }

    private Func createHexStringFunc(TreeNode spec) {
        return new HexStringFunc();
    }

    private Func createHexParseFunc(TreeNode spec) {
        return new HexParseFunc();
    }

}
