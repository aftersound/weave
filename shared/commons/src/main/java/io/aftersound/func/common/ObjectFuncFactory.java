package io.aftersound.func.common;

import io.aftersound.func.*;
import io.aftersound.schema.Field;
import io.aftersound.schema.Schema;
import io.aftersound.schema.SchemaHelper;
import io.aftersound.util.MapBuilder;
import io.aftersound.util.TreeNode;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static io.aftersound.func.FuncHelper.createCreationException;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjectFuncFactory extends MasterAwareFuncFactory {

    private static final List<Descriptor> DESCRIPTORS = Arrays.asList(
//            Descriptor.builder("OBJECT:CLASS", "TBD", "TBD")
//                    .withAliases("OBJ:CLASS")
//                    .build(),
//            Descriptor.builder("OBJECT:CLASS_LOADER", "TBD", "TBD")
//                    .withAliases("OBJ:CL")
//                    .build(),
//            Descriptor.builder("OBJECT:DECODE", "TBD", "TBD")
//                    .withAliases("OBJ:DECODE", "OBJ:DESER", "OBJECT:DESERIALIZE")
//                    .build(),
//            Descriptor.builder("OBJECT:ENCODE", "TBD", "TBD")
//                    .withAliases("OBJ:ENCODE", "OBJ:SER", "OBJECT:SERIALIZE")
//                    .build(),
//            Descriptor.builder("OBJECT:HASH", "TBD", "TBD")
//                    .withAliases("OBJ:HASH")
//                    .build(),
//            Descriptor.builder("OBJECT:ID", "TBD", "TBD")
//                    .withAliases("OBJ:ID")
//                    .build(),
//            Descriptor.builder("OBJECT:INFO", "TBD", "TBD")
//                    .withAliases("OBJ:INFO")
//                    .build()
    );

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        if ("ENUM:OF".equals(funcName)) {
            return createEnumOfFunc(spec);
        }

        if ("OBJ:CL".equals(funcName)) {
            return createObjectClassLoaderFunc(spec);
        }

        if ("OBJ:CLASS".equals(funcName)) {
            return createObjectClassFunc(spec);
        }

        if ("OBJ:DECODE".equals(funcName)) {
            return createDeserializeFunc(spec);
        }

        if ("OBJ:DESER".equals(funcName)) {
            return createDeserializeFunc(spec);
        }

        if ("OBJ:ENCODE".equals(funcName)) {
            return createSerializeFunc(spec);
        }

        if ("OBJ:FROM".equals(funcName)) {
            return createParseFromFunc(spec);
        }

        if ("OBJ:HASH".equals(funcName)) {
            return createObjectHashCodeFunc(spec);
        }

        if ("OBJ:ID".equals(funcName)) {
            return createObjectIdFunc(spec);
        }

        if ("OBJ:INFO".equals(funcName)) {
            return createObjectInfoFunc(spec);
        }

        if ("OBJ:SER".equals(funcName)) {
            return createSerializeFunc(spec);
        }

        if ("OBJECT:CLASS".equals(funcName)) {
            return createObjectClassFunc(spec);
        }

        if ("OBJECT:CLASS_LOADER".equals(funcName)) {
            return createObjectClassLoaderFunc(spec);
        }

        if ("OBJECT:DECODE".equals(funcName)) {
            return createDeserializeFunc(spec);
        }

        if ("OBJECT:DESERIALIZE".equals(funcName)) {
            return createDeserializeFunc(spec);
        }

        if ("OBJECT:ENCODE".equals(funcName)) {
            return createSerializeFunc(spec);
        }

        if ("OBJECT:HASH".equals(funcName)) {
            return createObjectHashCodeFunc(spec);
        }

        if ("OBJECT:ID".equals(funcName)) {
            return createObjectIdFunc(spec);
        }

        if ("OBJECT:INFO".equals(funcName)) {
            return createObjectInfoFunc(spec);
        }

        if ("OBJECT:SERIALIZE".equals(funcName)) {
            return createSerializeFunc(spec);
        }

        return null;
    }

    public static <IN, OUT> Func<IN, OUT> createParseFromFunc(Schema schema, Class<OUT> type) {
        return new ParseFromFunc(schema, type);
    }
    public static <IN, OUT> Func<IN, OUT> createParseFromFunc(Schema schema, Class<OUT> type, String directiveCategory) {
        return new ParseFromFunc(schema, type, directiveCategory);
    }

    static class EnumOfFunc<String, T extends Enum<T>> extends AbstractFuncWithHints<String, Enum<T>> {

        private final Map<String, Enum<T>> enums;

        public EnumOfFunc(Map<String, Enum<T>> enums) {
            this.enums = enums;
        }

        @Override
        public Enum<T> apply(String name) {
            return enums.get(name);
        }

    }

    static class ObjectDeserializeFunc<T> extends AbstractFuncWithHints<byte[], T> {

        private static final ObjectDeserializeFunc INSTANCE = new ObjectDeserializeFunc();

        @Override
        public T apply(byte[] source) {
            if (source == null || source.length == 0) {
                return null;
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(source);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                return (T) in.readObject();
            } catch (Exception e) {
                return null;
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }

                try {
                    bis.close();
                } catch (Exception e) {
                    // ignore close exception
                }
            }
        }

    }

    static class ObjectSerializeFunc<T> extends AbstractFuncWithHints<T, byte[]> {

        private static final ObjectSerializeFunc INSTANCE = new ObjectSerializeFunc();

        @Override
        public byte[] apply(T source) {
            if (source == null) {
                return null;
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out = null;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(source);
                out.flush();
                return bos.toByteArray();
            } catch (Exception e) {
                return null;
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    // ignore close exception
                }

                try {
                    bos.close();
                } catch (IOException ex) {
                    // ignore close exception
                }
            }
        }

    }

    static class ParseFromFunc<T> extends AbstractFuncWithHints<Object, T> {

        private final Func<Object, Map<String, Object>> delegate;
        private final Class<T> type;
        private final Constructor<T> objectConstructor;
        private final Map<String, Method> setterByFieldName;

        public ParseFromFunc(Schema schema, Class<T> type, String directiveCategory) {
            this.delegate = MapFuncFactory.createParseFromFunc(schema, directiveCategory);
            this.type = type;

            try {
                Constructor<T> constructor = type.getDeclaredConstructor();
                this.objectConstructor = constructor;
            } catch (Exception e) {
                String msg = String.format("Failed to obtain constructor of '%s'", type.getName());
                throw new CreationException(msg, e);
            }

            try {
                Map<String, Method> setters = new LinkedHashMap<>(schema.getFields().size());
                for (Method m : type.getDeclaredMethods()) {
                    String methodName = m.getName();
                    if (!Modifier.isStatic(m.getModifiers()) && Modifier.isPublic(m.getModifiers()) && methodName.startsWith("set")) {
                        String fieldName = methodName.substring(3);
                        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
                        setters.put(fieldName, m);
                    }
                }

                Map<String, Method> setterByFieldName = new LinkedHashMap<>(schema.getFields().size());
                for (Field f : schema.getFields()) {
                    Method setter = setters.get(f.getName());
                    if (setter != null) {
                        setterByFieldName.put(f.getName(), setter);
                    } else {
                        String msg = String.format("'%s' has no public setter for field '%s'", type.getName(), f.getName());
                        throw new CreationException(msg);
                    }
                }
                this.setterByFieldName = Collections.unmodifiableMap(setterByFieldName);
            } catch (Exception e) {
                String msg = String.format("Failed to obtain field setters of '%s'", type.getName());
                throw new CreationException(msg, e);
            }
        }

        public ParseFromFunc(Schema schema, Class<T> type) {
            this(schema, type, null);
        }

        @Override
        public T apply(Object source) {
            Map<String, Object> m = delegate.apply(source);
            if (m != null) {
                try {
                    T o = objectConstructor.newInstance();
                    for (Map.Entry<String, Method> e : setterByFieldName.entrySet()) {
                        String field = e.getKey();
                        Method method = e.getValue();
                        method.invoke(o, m.get(field));
                    }
                    return o;
                } catch (Exception e) {
                    String msg = String.format("Failed to create object of '%s'", type.getName());
                    throw new ExecutionException(msg, e);
                }
            } else {
                return null;
            }
        }

    }

    static class ObjectClassFunc<T> extends AbstractFuncWithHints<T, String> {

        private static final ObjectClassFunc INSTANCE = new ObjectClassFunc();

        @Override
        public String apply(T source) {
            return source != null ? source.getClass().getName() : null;
        }

    }

    static class ObjectIdFunc<T> extends AbstractFuncWithHints<T, String> {

        private static final ObjectIdFunc INSTANCE = new ObjectIdFunc();

        @Override
        public String apply(T source) {
            return source != null ? (source.getClass().getName() + "@" + Integer.toHexString(source.hashCode())) : null;
        }

    }

    static class ObjectClassLoaderFunc<T> extends AbstractFuncWithHints<T, String> {

        private static final ObjectClassLoaderFunc INSTANCE = new ObjectClassLoaderFunc();

        @Override
        public String apply(T source) {
            if (source != null) {
                ClassLoader cl = source.getClass().getClassLoader();
                return cl != null ? cl.toString() : "jdk.internal.loader.ClassLoaders$BootClassLoader@0000";
            } else {
                return null;
            }
        }

    }

    static class ObjectHashCodeFunc<T> extends AbstractFuncWithHints<T, Integer> {

        private static final ObjectHashCodeFunc INSTANCE = new ObjectHashCodeFunc();

        @Override
        public Integer apply(T source) {
            return source != null ? source.hashCode() : null;
        }

    }

    static class ObjectInfoFunc<T> extends AbstractFuncWithHints<T, Map<String, Object>> {

        private static final ObjectInfoFunc INSTANCE = new ObjectInfoFunc();

        @Override
        public Map<String, Object> apply(T source) {
            if (source != null) {
                return MapBuilder.<String, Object>linkedHashMap()
                        .put("id", ObjectIdFunc.INSTANCE.apply(source))
                        .put("className", ObjectClassFunc.INSTANCE.apply(source))
                        .put("hashCode", ObjectHashCodeFunc.INSTANCE.apply(source))
                        .put("classLoader", ObjectClassLoaderFunc.INSTANCE.apply(source))
                        .buildModifiable();
            } else {
                return null;
            }
        }

    }

    private Func createEnumOfFunc(TreeNode spec) {
        String enumClass = spec.getDataOfChildAt(0);
        try {
            Class<?> cls = Class.forName(enumClass);
            if (!cls.isEnum()) {
                throw new IllegalArgumentException(enumClass + " is not an Enum class");
            }
            Map<String, Enum> enums = new HashMap<>();
            for (java.lang.reflect.Field f : cls.getDeclaredFields()) {
                if (f.isEnumConstant()) {
                    enums.put(f.getName(), (Enum) f.get(null));
                }
            }
            return new EnumOfFunc(Collections.unmodifiableMap(enums));
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "ENUM:OF(enumClassName)",
                    "ENUM:OF(java.lang.concurrent.TimeUnit)",
                    e
            );
        }
    }

    private Func createSerializeFunc(TreeNode spec) {
        return new ObjectSerializeFunc<>();
    }

    private Func createDeserializeFunc(TreeNode spec) {
        return new ObjectDeserializeFunc<>();
    }

    private Func createParseFromFunc(TreeNode spec) {
        String className = spec.getDataOfChildAt(0);
        String schemaId = spec.getDataOfChildAt(1);
        String schemaRegistryId = spec.getDataOfChildAt(2, SchemaHelper.DEFAULT_SCHEMA_REGISTRY);

        try {
            Class<?> type = Class.forName(className);
            Schema schema = SchemaHelper.getRequiredSchema(schemaId, schemaRegistryId);
            return new ParseFromFunc(schema, type);
        } catch (Exception e) {
            throw createCreationException(
                    spec,
                    "OBJ:FROM(className,schemaId) or OBJ:FROM(className,schemaId,schemaRegistryId)",
                    "OBJ:FROM(io.aftersound.bean.Person,Person)",
                    e
            );
        }
    }

    private Func createObjectClassFunc(TreeNode spec) {
        return ObjectClassFunc.INSTANCE;
    }

    private Func createObjectIdFunc(TreeNode spec) {
        return ObjectIdFunc.INSTANCE;
    }

    private Func createObjectClassLoaderFunc(TreeNode spec) {
        return ObjectClassLoaderFunc.INSTANCE;
    }

    private Func createObjectHashCodeFunc(TreeNode spec) {
        return ObjectHashCodeFunc.INSTANCE;
    }

    private Func createObjectInfoFunc(TreeNode spec) {
        return ObjectInfoFunc.INSTANCE;
    }
}
