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

    private static final List<Descriptor> DESCRIPTORS = DescriptorHelper.getDescriptors(ObjectFuncFactory.class);

    @Override
    public List<Descriptor> getFuncDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public <IN, OUT> Func<IN, OUT> create(TreeNode spec) {
        final String funcName = spec.getData();

        switch (funcName) {
            case "ENUM:OF": {
                return createEnumOfFunc(spec);
            }
            case "OBJECT:CLASS_LOADER":
            case "OBJECT:CL":
            case "OBJ:CLASS_LOADER":
            case "OBJ:CL": {
                return createObjectClassLoaderFunc(spec);
            }
            case "OBJECT:CLASS":
            case "OBJ:CLASS": {
                return createObjectClassFunc(spec);
            }
            case "OBJECT:DECODE":
            case "OBJECT:DESER":
            case "OBJECT:DESERIALIZE":
            case "OBJ:DECODE":
            case "OBJ:DESER":
            case "OBJ:DESERIALIZE": {
                return createDeserializeFunc(spec);
            }
            case "OBJECT:FROM":
            case "OBJ:FROM": {
                return createParseFromFunc(spec);
            }
            case "OBJECT:HASH":
            case "OBJ:HASH": {
                return createObjectHashCodeFunc(spec);
            }
            case "OBJECT:ID":
            case "OBJ:ID": {
                return createObjectIdFunc(spec);
            }
            case "OBJECT:INFO":
            case "OBJ:INFO": {
                return createObjectInfoFunc(spec);
            }
            case "OBJECT:ENCODE":
            case "OBJECT:SER":
            case "OBJECT:SERIALIZE":
            case "OBJ:ENCODE":
            case "OBJ:SER":
            case "OBJ:SERIALIZE": {
                return createSerializeFunc(spec);
            }
            default: {
                return null;
            }
        }
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
