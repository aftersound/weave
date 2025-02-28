package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.msg.Message;
import io.aftersound.schema.*;
import io.aftersound.util.ResourceRegistry;
import io.aftersound.util.TreeNode;

import java.util.*;

import static io.aftersound.func.Constants.DEFAULT_SCHEMA_REGISTRY;
import static io.aftersound.func.FuncHelper.*;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
            Descriptor.builder("MAP:CONTAINS")
                    .withDescription("This function returns a string value as specified")
                    .withControls(
                            Field.stringFieldBuilder("stringLiteral")
                                    .withDescription("The string literal value that is expected to the output of the function")
                                    .build()
                    )
                    .withInput(Field.objectFieldBuilder("any").withDescription("Any input").build())
                    .withOutput(Field.stringFieldBuilder("stringValue").withDescription("The string value").build())
                    .withExamples(
                            Example.as(
                                    "MAP:CONTAINS(",
                                    "A function instance that returns 'hello world' when executed"
                            )
                    )
                    .build(),
            Descriptor.builder("MAP:FROM")
                    .withDescription("This function parses/maps an input into a map in according to given schema")
                    .withControls(
                            Field.stringFieldBuilder("schemaId")
                                    .withDescription("The identifier of the schema")
                                    .build(),
                            Field.stringFieldBuilder("schemaRegistryId")
                                    .withDescription("The identifier of the schema registry, which is expected to host the schema. When not specified, it defaults to 'defaultSchemaRegistry'")
                                    .withConstraint(Constraint.optional())
                                    .build()
                    )
                    .withInput(
                            Field.objectFieldBuilder("object")
                                    .withDescription("The input object that is expected to be parsed/mapped")
                                    .build()
                    )
                    .withOutput(
                            Field.stringFieldBuilder("map")
                            .withDescription("The output map")
                            .build()
                    )
                    .withExamples(
                            Example.as(
                                    "MAP:FROM(Person)",
                                    "This function instance parses/maps an input into a map in according to the schema 'Person' hosted in the schema registry 'defaultSchemaRegistry'"
                            ),
                            Example.as(
                                    "MAP:FROM(Person,HRSystem)",
                                    "This function instance parses/maps an input into a map in according to the schema 'Person' hosted in the schema registry 'HRSystem'"
                            )
                    )
                    .build()
//            Descriptor.builder("MAP:CONTAINS", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("MAP:GET", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("MAP:HAS_ANY_VALUE", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("MAP:HAS_KEY", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("MAP:HAS_VALUE", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("MAP:PUT", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("MAP:READ", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("MAP:VALIDATE", "TBD", "TBD")
//                    .build(),
//            Descriptor.builder("MAP:VALUES", "TBD", "TBD")
//                    .build()
    );

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
            return parse(source, schema.getFields());
        }

        private Map<String, Object> parse(Object source, List<Field> fields) {
            if (source == null) {
                return null;
            }

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
                final Object v;
                if (func.hasHint("ON", "TARGET")) {
                    v = func.apply(m);
                } else {
                    v = func.apply(source);
                }

                if (TypeHelper.isPrimitive(type)) {
                    m.put(fieldName, v);
                } else if (TypeHelper.isObject(type)) {
                    m.put(fieldName, parse(v, type.getFields()));
                } else if (TypeHelper.isArray(type)) {
                    m.put(fieldName, parseList(v, type.getElementType()));
                } else if (TypeHelper.isList(type)) {
                    m.put(fieldName, parseList(v, type.getElementType()));
                } else if (TypeHelper.isList(type)) {
                    m.put(fieldName, parseSet(v, type.getElementType()));
                }
            }

            return m;
        }

        private List<Object> parseList(Object o, Type elementType) {
            List<Object> list = new ArrayList<>();
            mapCollection(o, list, elementType);
            return list;
        }

        private Set<?> parseSet(Object o, Type elementType) {
            Set<Object> set = new LinkedHashSet<>();
            mapCollection(o, set, elementType);
            return set;
        }

        private void mapCollection(Object source, Collection<Object> target, Type elementType) {
            if (!(source instanceof Iterable<?>)) {
                return;
            }
            Iterator<?> iter = ((Iterable<?>) source).iterator();
            if (TypeHelper.isPrimitive(elementType)) {
                while (iter.hasNext()) {
                    target.add(iter.next());
                }
            } else if (TypeHelper.isObject(elementType)) {
                while (iter.hasNext()) {
                    Object e = iter.next();
                    target.add(parse(e, elementType.getFields()));
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
        String schemaName = spec.getDataOfChildAt(0);
        String resourceRegistryId = spec.getDataOfChildAt(1, DEFAULT_SCHEMA_REGISTRY);
        try {
            Schema schema = getRequiredSchema(schemaName, resourceRegistryId);
            return new FromFunc(schema);
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
        String schemaName = spec.getDataOfChildAt(0);
        String resourceRegistryId = spec.getDataOfChildAt(1, DEFAULT_SCHEMA_REGISTRY);

        assertNotNull(schemaName, "1st parameter '%s' cannot be null", "schema id/name");

        ResourceRegistry resourceRegistry = getRequiredDependency(resourceRegistryId, ResourceRegistry.class);
        Schema schema = resourceRegistry.get(schemaName);
        assertNotNull(schema, "Schema with id '%s' is not available in identified ResourceRegistry", schemaName);

        return new MapValidateFunc(schema, masterFuncFactory);
    }

    private Func createValuesFunc(TreeNode spec) {
        return new MapValuesFunc();
    }
}
