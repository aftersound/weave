package io.aftersound.schema;

public class TypeHelper {

    /**
     * Check if given type is array type
     *
     * @param type - the type to be checked
     * @return true if given type is array type
     */
    public static boolean isArray(Type type) {
        return ProtoTypes.ARRAY.name().equals(type.getName());
    }

    /**
     * Check if given type is container type
     *
     * @param type - the type to be checked
     * @return true if given type is container type
     */
    public static boolean isContainer(Type type) {
        return ProtoTypes.CONTAINER_TYPES.contains(type.getName());
    }

    /**
     * Check if given type is list type
     *
     * @param type - the type to be checked
     * @return true if given type is list type
     */
    public static boolean isList(Type type) {
        return ProtoTypes.LIST.name().equals(type.getName());
    }

    /**
     * Check if given type is map type
     *
     * @param type - the type to be checked
     * @return true if given type is map type
     */
    public static boolean isMap(Type type) {
        return ProtoTypes.MAP.name().equals(type.getName());
    }

    /**
     * Check if given type  is number type
     *
     * @param type - the type to be checked
     * @return true if given type is number type
     */
    public static boolean isNumberType(Type type) {
        return ProtoTypes.NUMBER_TYPES.contains(type.getName().toUpperCase());
    }

    /**
     * Check if given type  is number type
     *
     * @param typeName - the type name to be checked
     * @return true if given type name is number type
     */
    public static boolean isNumberType(String typeName) {
        return typeName != null && ProtoTypes.NUMBER_TYPES.contains(typeName.toUpperCase());
    }

    /**
     * Check if given type is object type
     *
     * @param type - the type to be checked
     * @return true if given type is object type
     */
    public static boolean isObject(Type type) {
        return isPlainObject(type) || isSpecificObject(type);
    }

    /**
     * Check if given type is plain object type (the name is 'OBJECT'
     *
     * @param type - the type to be checked
     * @return true if given type is plain object type
     */
    public static boolean isPlainObject(Type type) {
        return ProtoTypes.OBJECT.name().equals(type.getName());
    }

    /**
     * Check if given type is primitive type
     *
     * @param type - the type to be checked
     * @return true if given type is primitive type
     */
    public static boolean isPrimitive(Type type) {
        return ProtoTypes.PRIMITIVE_TYPES.contains(type.getName());
    }

    /**
     * Check if given type is set type
     *
     * @param type - the type to be checked
     * @return true if given type is set type
     */
    public static boolean isSet(Type type) {
        return ProtoTypes.SET.name().equals(type.getName());
    }

    /**
     * Check if given type is object type with specific name other than 'OBJECT'
     *
     * @param type - the type to be checked
     * @return true if given type is object type with specific name other than 'OBJECT'
     */
    public static boolean isSpecificObject(Type type) {
        return !(isPlainObject(type) || isPrimitive(type) || isContainer(type) || isMap(type) || isObject(type));
    }

}
