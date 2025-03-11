package io.aftersound.func.common;

import io.aftersound.func.AbstractFuncWithHints;
import io.aftersound.func.ExecutionException;
import io.aftersound.func.Func;
import io.aftersound.func.MasterAwareFuncFactory;
import io.aftersound.util.TreeNode;

import java.io.FileInputStream;
import java.io.InputStream;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class FileFuncFactory extends MasterAwareFuncFactory {

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
