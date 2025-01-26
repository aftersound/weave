package io.aftersound.func;

import io.aftersound.util.TreeNode;

import java.util.*;

@SuppressWarnings({"unchecked", "rawtypes"})
public class MapFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = List.of(
            Descriptor.builder("MAP:CONTAINS").build(),
            Descriptor.builder("MAP:GET").build(),
            Descriptor.builder("MAP:HAS_KEY").build(),
            Descriptor.builder("MAP:HAS_VALUE").build(),
            Descriptor.builder("MAP:READ").build(),
            Descriptor.builder("MAP:VALUES").build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode directive) {
        final String funcName = directive.getData();

        switch (funcName) {
            case "MAP:CONTAINS":
            case "MAP:HAS_KEY": {
                return createMapHasKeyFunc(directive);
            }
            case "MAP:GET": {
                return createMapGetFunc(directive);
            }
            case "MAP:HAS_VALUE": {
                return createMapHasValueFunc(directive);
            }
            case "MAP:READ": {
                return createMapReadFunc(directive);
            }
            case "MAP:VALUES": {
                return createMapValuesFunc(directive);
            }
            default: {
                return null;
            }
        }
    }

    static final class MapGetFunc extends AbstractFuncWithHints<Map<String, Object>, Object> {

        private final String key;

        public MapGetFunc(String key) {
            this.key = key;
        }

        @Override
        public Object apply(Map<String, Object> source) {
            if (source != null) {
                return source.get(key);
            } else {
                return null;
            }
        }
    }

    static final class MapHasKeyFunc extends AbstractFuncWithHints<Map<String, Object>, Boolean> {

        private final String key;

        public MapHasKeyFunc(String key) {
            this.key = key;
        }

        @Override
        public Boolean apply(Map<String, Object> source) {
            if (source != null) {
                return source.containsKey(key);
            }
            return Boolean.FALSE;
        }

    }

    static final class MapHasValueFunc extends AbstractFuncWithHints<Map<String, Object>, Boolean> {

        private final String key;
        private final Object value;

        public MapHasValueFunc(String key, Object value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Boolean apply(Map<String, Object> source) {
            if (source != null) {
                Object v = source.get(key);
                return value != null ? value.equals(v) : (v != null);
            }
            return Boolean.FALSE;
        }

    }

    static final class MapReadFunc extends AbstractFuncWithHints<Map<String, Object>, Map<String, Object>> {

        private final List<String> keys;

        public MapReadFunc(List<String> keys) {
            this.keys = Collections.unmodifiableList(keys);
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> source) {
            if (source != null) {
                Map<String, Object> r = new LinkedHashMap<>(keys.size());
                for (String key : keys) {
                    r.put(key, source.get(key));
                }
                return r;
            }
            return null;
        }

    }

    static final class MapValuesFunc extends AbstractFuncWithHints<Map<String, Object>, Collection<Object>> {

        public static final Func<Map<String, Object>, Collection<Object>> INSTANCE = new MapValuesFunc();

        @Override
        public Collection<Object> apply(Map<String, Object> source) {
            return source != null ? source.values() : null;
        }

    }

    private Func createMapGetFunc(TreeNode spec) {
        return new MapGetFunc(spec.getDataOfChildAt(0));
    }

    private Func createMapHasKeyFunc(TreeNode spec) {
        final String key = spec.getDataOfChildAt(0);
        return new MapHasKeyFunc(key);
    }

    private Func createMapHasValueFunc(TreeNode spec) {
        final String key = spec.getDataOfChildAt(0);
        final TreeNode valueSpec = spec.getChildAt(1);
        final Object value;
        if (valueSpec != null) {
            if (valueSpec.getChildren() == null) {
                value = valueSpec.getData();
            } else {
                value = masterFuncFactory.create(valueSpec).apply(null);
            }
        } else {
            value = null;
        }
        return new MapHasValueFunc(key, value);
    }

    private Func createMapReadFunc(TreeNode spec) {
        return new MapReadFunc(spec.getDataOfChildren());
    }

    private static Func createMapValuesFunc(TreeNode spec) {
        return MapValuesFunc.INSTANCE;
    }
}
