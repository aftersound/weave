package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.msg.Message;
import io.aftersound.schema.*;
import io.aftersound.util.TreeNode;

import java.util.*;

import static io.aftersound.func.FuncHelper.createCreationException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = DescriptorHelper.getDescriptors(MapFuncFactory.class);

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        switch (funcName) {
            case "MAP:CONTAINS":
            case "MAP:HAS_KEY": {
                return createHasKeyFunc(spec);
            }
            case "MAP:FROM": {
                return createFromFunc(spec);
            }
            case "MAP:HAS_ANY_VALUE": {
                return createHasAnyValueFunc(spec);
            }
            case "MAP:HAS_VALUE": {
                return createHasValueFunc(spec);
            }
            case "MAP:GET" :{
                return createGetFunc(spec);
            }
            case "MAP:KEYS" :{
                return createKeysFunc(spec);
            }
            case "MAP:PUT": {
                return createPutFunc(spec);
            }
            case "MAP:READ": {
                return createReadFunc(spec);
            }
            case "MAP:VALIDATE": {
                return createValidateFunc(spec);
            }
            case "MAP:VALUES": {
                return createValuesFunc(spec);
            }
            default: {
                return null;
            }
        }

    }

    public static <S> Func<S, Map<String, Object>> createParseFromFunc(Schema schema) {
        return new FromFunc(schema);
    }

    public static <S> Func<S, Map<String, Object>> createParseFromFunc(Schema schema, String directiveCategory) {
        return new FromFunc(schema, directiveCategory);
    }

    static final class FromFunc<S> extends AbstractFuncWithHints<S, Map<String, Object>> {

        private final Schema schema;
        private final String directiveCategory;

        public FromFunc(Schema schema, String directiveCategory) {
            this.schema = schema;
            this.directiveCategory = directiveCategory != null ? directiveCategory : "TRANSFORM";
        }

        public FromFunc(Schema schema) {
            this(schema, null);
        }

        @Override
        public Map<String, Object> apply(Object source) {
            if (source == null) {
                return null;
            }
            return parse(InputContext.of(source, source), schema.getFields());
        }

        private Map<String, Object> parse(InputContext context, List<Field> fields) {
            if (context == null) {
                return null;
            }

            final Object root = context.getRoot();

            Map<String, Object> m = new LinkedHashMap<>();
            for (Field field : fields) {
                final String fieldName = field.getName();
                final Type type = field.getType();
                Directive directive = field.directives().first(d -> d.getCategory().equals(directiveCategory));
                if (directive == null) {
                    String msg = String.format("Field '%s' has no directive with category '%s'", fieldName, directiveCategory);
                    throw new IllegalStateException(msg);
                }
                FuncWithHints<Object, Object> func = (FuncWithHints<Object, Object>) directive.function();
                final Object current;
                if (func.hasHint("ON", "TARGET")) {
                    current = func.apply(m);
                } else {
                    current = func.apply(context);
                }

                if (TypeHelper.isPrimitive(type)) {
                    m.put(fieldName, current);
                } else if (TypeHelper.isObject(type)) {
                    m.put(fieldName, parse(InputContext.of(root, current), type.getFields()));
                } else if (TypeHelper.isArray(type)) {
                    m.put(fieldName, parseList(InputContext.of(root, current), type.getElementType()));
                } else if (TypeHelper.isList(type)) {
                    m.put(fieldName, parseList(InputContext.of(root, current), type.getElementType()));
                } else if (TypeHelper.isList(type)) {
                    m.put(fieldName, parseSet(InputContext.of(root, current), type.getElementType()));
                }
            }

            return m;
        }

        private List<Object> parseList(InputContext context, Type elementType) {
            List<Object> list = new ArrayList<>();
            mapCollection(context, list, elementType);
            return list;
        }

        private Set<?> parseSet(InputContext context, Type elementType) {
            Set<Object> set = new LinkedHashSet<>();
            mapCollection(context, set, elementType);
            return set;
        }

        private void mapCollection(InputContext context, Collection<Object> target, Type elementType) {
            final Object root = context.getRoot();
            final Object current = context.getCurrent();
            if (!(current instanceof Iterable<?>)) {
                return;
            }
            Iterator<?> iter = ((Iterable<?>) current).iterator();
            if (TypeHelper.isPrimitive(elementType)) {
                while (iter.hasNext()) {
                    target.add(iter.next());
                }
            } else if (TypeHelper.isObject(elementType)) {
                while (iter.hasNext()) {
                    Object e = iter.next();
                    target.add(parse(InputContext.of(root, e), elementType.getFields()));
                }
            }
            // TODO other element types
        }

    }

    static final class GetFunc extends AbstractFuncWithHints<Map<String, Object>, Object> {

        private final String key;

        public GetFunc(String key) {
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

    static final class HasAnyValueFunc extends AbstractFuncWithHints<Map<String, Object>, Boolean> {

        private final String key;
        private final Set<Object> values;

        public HasAnyValueFunc(String key, Set<Object> values) {
            this.key = key;
            this.values = values;
        }

        @Override
        public Boolean apply(Map<String, Object> source) {
            if (source != null) {
                Object v = source.get(key);
                return values.contains(v);
            }
            return Boolean.FALSE;
        }

    }

    static final class HasKeyFunc extends AbstractFuncWithHints<Map<String, Object>, Boolean> {

        private final String key;

        public HasKeyFunc(String key) {
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

    static final class HasValueFunc extends AbstractFuncWithHints<Map<String, Object>, Boolean> {

        private final String key;
        private final Object value;

        public HasValueFunc(String key, Object value) {
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

    static final class KeysFunc extends AbstractFuncWithHints<Map<Object, Object>, Collection<Object>> {

        @Override
        public Collection<Object> apply(Map<Object, Object> source) {
            return source != null ? source.keySet() : null;
        }

    }

    static final class MapPutFunc extends AbstractFuncWithHints<Map<String, Object>, Map<String, Object>> {

        private final String key;
        private final Func<Object, Object> valueFunc;

        public MapPutFunc(String key, Func<Object, Object> valueFunc) {
            this.key = key;
            this.valueFunc = valueFunc;
        }

        @Override
        public Map<String, Object> apply(Map<String, Object> source) {
            if (source != null) {
                source.put(key, valueFunc.apply(null));
            }
            return source;
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

    static final class MapValidateFunc extends AbstractFuncWithHints<Map<String, Object>, List<Message>> {

        private final Schema schema;
        private final FuncFactory masterFuncFactory;

        public MapValidateFunc(Schema schema, FuncFactory masterFuncFactory) {
            this.schema = schema;
            this.masterFuncFactory = masterFuncFactory;
        }

        @Override
        public List<Message> apply(Map<String, Object> source) {
            List<Message> messages = new ArrayList<>();

            // validation in according to field definition
            for (Field field : schema.getFields()) {
                validate(source, field, messages);
            }

            // validation in according to explicit directives
            List<Directive> validations = schema.directives().filter(d -> "VALIDATION".equals(d.getCategory()));
            if (!validations.isEmpty()) {
                validate(source, validations, messages);
            }

            return messages;
        }

        private void validate(Map<String, Object> source, Field field, List<Message> messages) {
            final Object fieldValue = source.get(field.getName());

            // validation in according to constraint directive
            Constraint constraint = field.getConstraint();
            Constraint.Type constraintType = constraint != null ? constraint.getType() : null;
            if (constraintType == null) {
                constraintType = Constraint.Type.Optional;
            }
            switch (constraintType) {
                case Required: {
                    if (fieldValue == null) {
                        messages.add(missingRequiredField(field));
                    }
                    break;
                }
                case SoftRequired: {
                    if (fieldValue == null) {
                        Func<Map<String, Object>, Boolean> valueFunc = masterFuncFactory.create(constraint.getWhen().getCondition());
                        Boolean required = valueFunc.apply(source);
                        if (required != null && required) {
                            messages.add(missingRequiredField(field));
                        }
                    }
                    break;
                }
                default: {
                    break;
                }
            }

            // validation in according to explicit validation directives
            List<Directive> validations = field.directives().filter(d -> "VALIDATION".equals(d.getCategory()));
            if (!validations.isEmpty()) {
                validate(source, validations, messages);
            }
        }

        private Message missingRequiredField(Field field) {
            return Message.builder()
                    .withContent(String.format("'%s': required by missing a value", field.path()))
                    .build();
        }

        private void validate(Map<String, Object> source, List<Directive> validations, List<Message> messages) {
            for (Directive validation : validations) {
                Func<Map<String, Object>, Boolean> conditionFunc = validation.function();

                // TODO:
                //   attach resources
                // conditionFunc.attachResources();

                Boolean met = conditionFunc.apply(source);
                if (met == null || !met) {
                    Message msg = validation.getMessage().copy();
                    // TODO: validation.message template handling
                    messages.add(msg);
                }
            }
        }

    }

    static final class MapValuesFunc extends AbstractFuncWithHints<Map<String, Object>, Collection<Object>> {

        @Override
        public Collection<Object> apply(Map<String, Object> source) {
            return source != null ? source.values() : null;
        }

    }

    private Func createFromFunc(TreeNode spec) {
        final String schemaId = spec.getDataOfChildAt(0);
        final String directiveCategory = spec.getDataOfChildAt(1, "TRANSFORM");
        final String schemaRegistryId = spec.getDataOfChildAt(2, SchemaHelper.DEFAULT_SCHEMA_REGISTRY);
        try {
            Schema schema = SchemaHelper.getRequiredSchema(schemaId, schemaRegistryId);
            return new FromFunc(schema, directiveCategory);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "MAP:FROM(schemaId) or MAP:FROM(schemaId,schemaRegistryId)",
                    "MAP:FROM(Person)"
            );
        }
    }

    private Func createGetFunc(TreeNode spec) {
        return new GetFunc(spec.getDataOfChildAt(0));
    }

    private Func createHasAnyValueFunc(TreeNode spec) {
        // examples
        //   MAP:HAS_ANY_VALUE(firstName,Nikola,Thomas)
        //   MAP:HAS_ANY_VALUE(count,INT(1),INT(2))

        // key
        final String key = spec.getDataOfChildAt(0);
        if (key == null) {
            throw new CreationException("key is missing");
        }

        // values
        Set<Object> values = new HashSet<>();
        for (TreeNode valueSpec : spec.getChildren(1)) {
            if (valueSpec.getChildren() == null) {
                // treat it as string
                values.add(valueSpec.getData());
            } else {
                // treat it as literal func which needs to be evaluated
                Object value = masterFuncFactory.create(valueSpec).apply(null);
                if (value != null) {
                    values.add(value);
                }
            }
        }
        return new HasAnyValueFunc(key, values);
    }

    private Func createHasKeyFunc(TreeNode spec) {
        final String key = spec.getDataOfChildAt(0);
        return new HasKeyFunc(key);
    }

    private Func createHasValueFunc(TreeNode spec) {
        final String key = spec.getDataOfChildAt(0);
        final TreeNode valueSpec = spec.getChildAt(1);

        if (key == null) {
            throw createCreationException(
                    spec,
                    "MAP:HAS_VALUE(key,valueFuncSpec[optional])",
                    "MAP:HAS_VALUE(firstName,STR(Nikola))"
            );
        }

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

        return new HasValueFunc(key, value);
    }

    private Func createKeysFunc(TreeNode spec) {
        return new KeysFunc();
    }

    private Func createPutFunc(TreeNode spec) {
        final String key = spec.getDataOfChildAt(0);
        final TreeNode valueSpec = spec.getChildAt(1);
        Func<Object, Object> valueFunc;
        if (valueSpec.getChildren() == null) {
            // treat it as string
            valueFunc = masterFuncFactory.create("STR(" + valueSpec.getData() + ")");
        } else {
            // treat it as literal func which needs to be evaluated
            valueFunc = masterFuncFactory.create(valueSpec);
        }
        return new MapPutFunc(key, valueFunc);
    }

    private Func createReadFunc(TreeNode spec) {
        return new MapReadFunc(spec.getDataOfChildren());
    }

    private Func createValidateFunc(TreeNode spec) {
        final String schemaName = spec.getDataOfChildAt(0);
        final String schemaRegistryId = spec.getDataOfChildAt(1, SchemaHelper.DEFAULT_SCHEMA_REGISTRY);
        try {
            Schema schema = SchemaHelper.getRequiredSchema(schemaName, schemaRegistryId);
            return new MapValidateFunc(schema, masterFuncFactory);
        } catch (Exception e) {
            throw FuncHelper.createCreationException(
                    spec,
                    "MAP:VALIDATE(schemaId) or MAP:VALIDATE(schemaId,schemaRegistryId)",
                    "MAP:VALIDATE(Person) or MAP:VALIDATE(Person,schema-registry)"
            );
        }
    }

    private Func createValuesFunc(TreeNode spec) {
        return new MapValuesFunc();
    }
}
