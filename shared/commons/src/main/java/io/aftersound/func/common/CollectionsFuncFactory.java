package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.Field;
import io.aftersound.schema.Type;
import io.aftersound.util.TreeNode;

import java.util.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
class CollectionsFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Collections.singletonList(
            Descriptor.builder("LIST:AS")
                    .withDescription("Create a list out of input collection")
                    .withInput(Field.builder("collection", Type.builder("Collection").build()).build())
                    .withOutput(Field.listFieldBuilder("list", Type.builder("Any").build()).build())
                    .withExamples(
                            Example.as(
                                    "LIST:AS()",
                                    "Create a list out of input collection"
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

        if ("LIST:AS".equals(funcName)) {
            return createAsListFunc(spec);
        }

        return null;
    }

    static class AsListFunc extends AbstractFuncWithHints<Object, List<Object>> {

        @Override
        public List<Object> apply(Object source) {
            if (source instanceof List) {
                return (List<Object>) source;
            }

            if (source instanceof Collection) {
                return new ArrayList<>((Collection<Object>) source);
            }

            return null;
        }
    }

    private Func createAsListFunc(TreeNode spec) {
        return new AsListFunc();
    }

}
