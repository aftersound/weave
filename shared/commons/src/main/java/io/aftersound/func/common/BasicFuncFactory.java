package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.Field;
import io.aftersound.schema.Type;
import io.aftersound.util.MapBuilder;
import io.aftersound.util.TreeNode;

import java.util.*;

import static io.aftersound.func.FuncHelper.createCreationException;
import static io.aftersound.func.FuncHelper.createParseFunc;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class BasicFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("_")
                    .withDescription("Pass the input through and return as is")
                    .withExamples(
                            Example.as(
                                    "_()",
                                    "Pass the input through and return as is"
                            )
                    )
                    .build(),

            Descriptor.builder("CHAIN")
                    .withAliases("CHAINED")
                    .withDescription("Process input by chain of value funcs")
                    .withControls(
                            Field.stringFieldBuilder("funcChain")
                                    .withDescription("One or more value func expressions in order")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "CHAIN(DATE:FROM(Long),DATE:FORMAT(YYYY-MM-DD))",
                                    "Convert long to java.util.Date then format date into 'YYYY-MM-DD' format"
                            )
                    )
                    .build(),

            Descriptor.builder("DEFAULT")
                    .withDescription("Provide a default value if input is null or missing")
                    .withControls(
                            Field.builder("defaultValue", Type.builder("varies").build())
                                    .withDescription("The default value func to apply when input is null or missing")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "DEFAULT(INT(10))",
                                    "Return integer value 10 when input is null"
                            )
                    )
                    .build(),

            Descriptor.builder("FILTER")
                    .withDescription("Filter input based on a predicate")
                    .withControls(
                            Field.stringFieldBuilder("predicate")
                                    .withDescription("The predicate to evaluate for filtering")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "FILTER(predicate)",
                                    "Filter input based on the specified predicate"
                            )
                    )
                    .build(),

            Descriptor.builder("LABEL")
                    .withDescription("Assign a label to the input")
                    .withControls(
                            Field.stringFieldBuilder("labelName")
                                    .withDescription("The name of the label to assign")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "LABEL(labelName)",
                                    "Assign the specified label to the input"
                            )
                    )
                    .build(),

            Descriptor.builder("MAPPING")
                    .withDescription("Map input values to corresponding outputs")
                    .withControls(
                            Field.builder("keyType", Type.builder("varies").build()).build(),
                            Field.builder("valueType", Type.builder("varies").build()).build(),
                            Field.builder("k1", Type.builder("varies").build()).build(),
                            Field.builder("v1", Type.builder("varies").build()).build()
                    )
                    .withExamples(
                            Example.as(
                                    "MAPPING(String,Integer,F,0,M,1)",
                                    "Map the input values based on the specified mapping definition, which return int 0 for input F or return int 1 for input M"
                            )
                    )
                    .build(),

            Descriptor.builder("NAME")
                    .withDescription("Name or rename a particular input or output")
                    .withControls(
                            Field.stringFieldBuilder("name")
                                    .withDescription("The name to assign")
                                    .build()
                    )
                    .withExamples(
                            Example.as(
                                    "NAME(newName)",
                                    "Name the input or output as 'newName'"
                            )
                    )
                    .build(),

            Descriptor.builder("NULL")
                    .withDescription("Explicitly set the value as null")
                    .withExamples(
                            Example.as(
                                    "NULL()",
                                    "Return null explicitly"
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

        if ("_".equals(funcName)) {
            return createPassThroughFunc(spec);
        }

        if ("CHAIN".equals(funcName) || "CHAINED".equals(funcName)) {
            return createChainFunc(spec);
        }

        if ("DEFAULT".equals(funcName)) {
            return createDefaultFunc(spec);
        }

        if ("FILTER".equals(funcName)) {
            return createFilterFunc(spec);
        }

        if ("LABEL".equals(funcName)) {
            return createLabelFunc(spec);
        }

        if ("MAPPING".equals(funcName)) {
            return createMappingFunc(spec);
        }

        if ("NAME".equals(funcName)) {
            return createNameFunc(spec);
        }

        if ("NULL".equals(funcName)) {
            return createNullFunc(spec);
        }

        return null;
    }

    static class PassThroughValueFunc extends AbstractFuncWithHints<Object, Object> {

        @Override
        public Object apply(Object source) {
            return source;
        }

    }

    static class DefaultValueFunc extends AbstractFuncWithHints<Object, Object> {

        private final Func<Object, Object> valueFunc;

        public DefaultValueFunc(Func<Object, Object> valueFunc) {
            this.valueFunc = valueFunc;
        }

        @Override
        public Object apply(Object source) {
            if (source != null) {
                return source;
            } else {
                return valueFunc.apply(null);
            }
        }
    }

    static class FilterFunc extends AbstractFuncWithHints<Object, Object> {

        private final Func<Object, Boolean> predicate;

        public FilterFunc(Func<Object, Boolean> predicate) {
            this.predicate = predicate;
        }


        @Override
        public Object apply(Object source) {
            if (source != null) {
                Boolean p = predicate.apply(source);
                return p != null && p ? source : null;
            }
            return null;
        }
    }

    static class LabelFunc<S, T> extends AbstractFuncWithHints<S, T> {

        private final Func<S, Boolean> predicate;
        private final T label;

        public LabelFunc(Func<S, Boolean> predicate, T label) {
            this.predicate = predicate;
            this.label = label;
        }

        @Override
        public T apply(S source) {
            Boolean satisfied = predicate.apply(source);
            return satisfied != null && satisfied ? label : null;
        }

    }

    static final class MappingFunc<S, T> extends AbstractFuncWithHints<S, T> {

        private final Map<S, T> mapping;

        public MappingFunc(Map<S, T> mapping) {
            this.mapping = mapping;
        }

        @Override
        public T apply(S source) {
            if (source != null) {
                return mapping.get(source);
            } else {
                return null;
            }
        }
    }

    static class NameFunc extends AbstractFuncWithHints<Object, Map<String, Object>> {

        private final String name;

        public NameFunc(String name) {
            this.name = name;
        }

        @Override
        public Map<String, Object> apply(Object source) {
            return MapBuilder.<String, Object>hashMap(1).put(name, source).build();
        }

    }

    static class NullFunc extends AbstractFuncWithHints<Object, Object> {

        @Override
        public Object apply(Object source) {
            return null;
        }
    }

    private Func createPassThroughFunc(TreeNode spec) {
        return new PassThroughValueFunc();
    }

    private Func createChainFunc(TreeNode spec) {
        List<TreeNode> children = spec.getChildren();
        if (children == null || children.isEmpty()) {
            throw new CreationException(spec + " is not valid");
        }

        List<Func<Object, Object>> chain = new ArrayList<>(children.size());
        for (TreeNode child : children) {
            Func<Object, Object> func = masterFuncFactory.create(child);
            chain.add(func);
        }
        return new ChainFunc(chain);
    }

    private Func createDefaultFunc(TreeNode spec) {
        TreeNode valueFuncSpec = spec.getChildAt(0);
        if (valueFuncSpec == null) {
            throw createCreationException(spec, "DEFAULT(value func spec)", "DEFAULT(INT(10))");
        }
        return new DefaultValueFunc(masterFuncFactory.create(valueFuncSpec));
    }

    private Func createFilterFunc(TreeNode spec) {
        TreeNode predicateSpec = spec.getChildAt(0);
        if (predicateSpec == null) {
            throw createCreationException(spec, "FILTER(predicate func spec)", "FILTER(EQ(MAP:GET(isInventor),BOOL(true)))");
        }
        Func<Object, Boolean> predicate = masterFuncFactory.create(predicateSpec);
        return new FilterFunc(predicate);
    }

    private Func createLabelFunc(TreeNode spec) {
        try {
            TreeNode predicateFuncSpec = spec.getChildAt(0);
            TreeNode labelValueFuncSpec = spec.getChildAt(1);
            Func<?, Boolean> predicateFunc = masterFuncFactory.create(predicateFuncSpec);
            Object label = masterFuncFactory.create(labelValueFuncSpec).apply(null);
            return new LabelFunc(predicateFunc, label);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "LABEL(predicate func spec, label value func sped)",
                    "LABEL(MAP:GET(isInventor),STR(INVENTOR))",
                    e
            );
        }
    }

    private Func createMappingFunc(TreeNode spec) {
        // MAPPING(keyType,valueType,k1,v1,k2,v2,...,kn,vn)
        try {
            String keyType = spec.getDataOfChildAt(0);
            Func<String, Object> keyParser = createParseFunc(keyType, masterFuncFactory);
            if (keyParser == null) {
                throw new NullPointerException("key parser is null");
            }

            String valueType = spec.getDataOfChildAt(1);
            Func<String, Object> valueParser = createParseFunc(valueType, masterFuncFactory);
            if (valueParser == null) {
                throw new NullPointerException("value parser is null");
            }

            List<TreeNode> kvSequence = spec.getChildren(2);
            if (kvSequence == null || kvSequence.isEmpty()) {
                throw new Exception("No key/value pair");
            }
            if (kvSequence.size() % 2 == 1) {
                throw new Exception("key/value pairing is not aligned");
            }

            Map<Object, Object> mapping = new HashMap<>();
            for (int i = 0; i < kvSequence.size() / 2; i++) {
                int keyIndex = i * 2;
                int valueIndex = keyIndex + 1;
                String keyStr = kvSequence.get(keyIndex).getData();
                String valueStr = kvSequence.get(valueIndex).getData();
                Object key = keyParser.apply(keyStr);
                Object value = valueParser.apply(valueStr);
                mapping.put(key, value);
            }

            return new MappingFunc<>(mapping);

        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "MAPPING(keyType,valueType,k1,v1,k1,v2,...)",
                    "MAPPING(String,Integer,Admin,1,Operator,2,Audit,3)",
                    e
            );
        }
    }

    private Func createNameFunc(TreeNode spec) {
        // NAME(<name>)
        String name = spec.getDataOfChildAt(0);
        if (name != null && !name.isEmpty()) {
            return new NameFunc(name);
        }
        throw createCreationException(
                spec,
                "NAME(name literal)",
                "NAME(caller)"
        );
    }

    private Func createNullFunc(TreeNode spec) {
        return new NullFunc();
    }


}
