package io.aftersound.func.common;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.Descriptor;
import io.aftersound.func.Func;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.util.TreeNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class InputStreamFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("IS:READ_BYTES")
                    .build(),
            Descriptor.builder("IS:READ_LINES")
                    .build(),
            Descriptor.builder("IS:READ_STRING")
                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("IS:READ_BYTES".equals(funcName)) {
            return createReadBytesFunc(spec);
        }

        if ("IS:READ_LINES".equals(funcName)) {
            return createReadLinesFunc(spec);
        }

        if ("IS:READ_STRING".equals(funcName)) {
            return createReadStringFunc(spec);
        }

        return null;
    }

    static class ReadBytesFunc extends AbstractFuncWithHints<InputStream, byte[]> {

        @Override
        public byte[] apply(InputStream source) {
            if (source != null) {
                try {
                    byte[] bytes = new byte[source.available()];
                    source.read(bytes);
                    return bytes;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                return null;
            }
        }

    }

    static class ReadLinesFunc extends AbstractFuncWithHints<InputStream, List<String>> {

        @Override
        public List<String> apply(InputStream source) {
            if (source != null) {
                try (InputStreamReader isr = new InputStreamReader(source); BufferedReader br = new BufferedReader(isr)) {
                    List<String> lines = new ArrayList<>();
                    String line;
                    while ((line = br.readLine()) != null) {
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

    static class ReadStringFunc extends AbstractFuncWithHints<InputStream, String> {

        private static final int BUFFER_SIZE = 1024;

        @Override
        public String apply(InputStream source) {
            if (source != null) {
                char[] buffer = new char[BUFFER_SIZE];
                StringBuilder sb = new StringBuilder();
                // TODO: different Charset?
                try (Reader reader = new InputStreamReader(source, StandardCharsets.UTF_8)) {
                    for (int numRead; (numRead = reader.read(buffer, 0, buffer.length)) > 0; ) {
                        sb.append(buffer, 0, numRead);
                    }
                    return sb.toString();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }

    private Func createReadBytesFunc(TreeNode spec) {
        return new ReadBytesFunc();
    }

    private Func createReadLinesFunc(TreeNode spec) {
        return new ReadLinesFunc();
    }

    private Func createReadStringFunc(TreeNode spec) {
        return new ReadStringFunc();
    }

}
