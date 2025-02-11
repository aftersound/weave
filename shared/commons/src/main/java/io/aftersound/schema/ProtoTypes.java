package io.aftersound.schema;

import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static io.aftersound.schema.ProtoType.*;

public final class ProtoTypes {

    public static final ProtoType ARRAY = new ProtoType("ARRAY", CONTAINER);

    public static final ProtoType BOOLEAN = new ProtoType("BOOLEAN", PRIMITIVE);

    public static final ProtoType BYTES = new ProtoType("BYTES", PRIMITIVE);

    public static final ProtoType CHAR = new ProtoType("CHAR", PRIMITIVE);

    public static final ProtoType DATE = new ProtoType("DATE", PRIMITIVE);

    public static final ProtoType DOUBLE = new ProtoType("DOUBLE", PRIMITIVE | NUMBER);

    public static final ProtoType FLOAT = new ProtoType("FLOAT", PRIMITIVE | NUMBER);

    public static final ProtoType INTEGER = new ProtoType("INTEGER", PRIMITIVE | NUMBER);

    public static final ProtoType LIST = new ProtoType("LIST", CONTAINER);

    public static final ProtoType LONG = new ProtoType("LONG", PRIMITIVE | NUMBER);

    public static final ProtoType MAP = new ProtoType("MAP");

    public static final ProtoType SET = new ProtoType("SET", CONTAINER);

    public static final ProtoType SHORT = new ProtoType("SHORT", PRIMITIVE | NUMBER);

    public static final ProtoType STRING = new ProtoType("STRING", PRIMITIVE);

    public static final ProtoType OBJECT = new ProtoType("OBJECT");

    public static final Set<String> PRIMITIVE_TYPES;
    public static final Set<String> NUMBER_TYPES;
    public static final Set<String> CONTAINER_TYPES;

    static {
        try {
            Set<String> primitiveTypes = new LinkedHashSet<>();
            Set<String> numberTypes = new LinkedHashSet<>();
            Set<String> containerTypes = new LinkedHashSet<>();
            for (java.lang.reflect.Field field : ProtoTypes.class.getDeclaredFields()) {
                final int modifiers = field.getModifiers();
                if (field.getType() == ProtoType.class &&
                        (modifiers & Modifier.PUBLIC) == Modifier.PUBLIC &&
                        (modifiers & Modifier.STATIC) == Modifier.STATIC &&
                        (modifiers & Modifier.FINAL) == Modifier.FINAL) {
                    field.setAccessible(true);
                    ProtoType protoType = (ProtoType) field.get(null);
                    if (protoType.isPrimitive()) {
                        primitiveTypes.add(protoType.name());
                    }
                    if (protoType.isNumber()) {
                        numberTypes.add(protoType.name());
                    }
                    if (protoType.isContainer()) {
                        containerTypes.add(protoType.name());
                    }
                }
            }
            PRIMITIVE_TYPES = Collections.unmodifiableSet(primitiveTypes);
            NUMBER_TYPES = Collections.unmodifiableSet(primitiveTypes);
            CONTAINER_TYPES = Collections.unmodifiableSet(containerTypes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
