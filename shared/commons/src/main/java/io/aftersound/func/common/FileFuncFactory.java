package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.Field;
import io.aftersound.schema.Type;
import io.aftersound.util.TreeNode;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FileFuncFactory extends MasterAwareFuncFactory {

        private static final List<Descriptor> DESCRIPTORS = Collections.singletonList(
                Descriptor.builder("FILE:READ")
                        .withDescription("")
                        .withControls(
                                Field.stringFieldBuilder("contentReadFunc")
                                        .withDescription("the spec of function that reads content of file input stream")
                                        .build()
                        )
                        .withInput(
                                Field.stringFieldBuilder("absoluteFilePath").build()
                        )
                        .withOutput(
                                Field.builder("content", Type.builder("varies").build())
                                        .withDescription("the content of the file, the type of which is dictated by the content read function")
                                        .build()
                        )
                        .withExamples(
                                Example.as(
                                        "FILE:READ(IS:READ_BYTES())",
                                        "Read the content of specified file as byte array"
                                ),
                                Example.as(
                                        "FILE:READ(IS:READ_LINES())",
                                        "Read the content of specified file as lines of string"
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

        if ("FILE:READ".equals(funcName)) {
            return createFileReadFunc(spec);
        }

        return null;
    }

    static class FileReadFunc extends AbstractFuncWithHints<String, Object> {

        private final Func<InputStream, Object> func;

        public FileReadFunc(Func<InputStream, Object> func) {
            this.func = func;
        }

        @Override
        public Object apply(String source) {
            if (source != null) {
                try (InputStream is = new FileInputStream(source)) {
                    return func.apply(is);
                } catch (Exception e) {
                    throw new ExecutionException(String.format("Failed to read file %s", source), e);
                }
            }
            return null;
        }

    }

    private Func createFileReadFunc(TreeNode spec) {
        final Func<InputStream, Object> func = masterFuncFactory.create(spec.getChildAt(0));
        return new FileReadFunc(func);
    }

}
