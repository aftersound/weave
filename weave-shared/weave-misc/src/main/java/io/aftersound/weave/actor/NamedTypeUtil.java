package io.aftersound.weave.actor;

import io.aftersound.weave.common.NamedType;
import io.aftersound.weave.common.NamedTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Util which deals with {@link NamedType} and {@link NamedTypes} attached with ACTOR types
 */
public final class NamedTypeUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamedTypeUtil.class);

    /**
     * Extract NamedType with generic type BT from static final field with specified name in specified class
     * @param clazz
     *          - target class which is expected to have field, of NamedType with generic type BT, with specified name
     * @param fieldName
     *          - name of static final field, which is expected to be NamedType with generic type BT
     * @param baseType
     *          - base type
     * @param <BT>
     *          - generic form of base type
     * @param <T>
     *          - generic form of type, extended from base type
     * @return
     *          an {@link NamedType} with generic type BT
     * @throws Exception
     *          throws exception if any occurs
     */
    public static <BT, T extends BT> NamedType<BT> extractNamedType(
            Class<?> clazz,
            String fieldName,
            Class<T> baseType) throws Exception {

        if (clazz == null || baseType == null || fieldName == null || fieldName.isEmpty()) {
            throw new IllegalArgumentException("neither clazz, baseType nor fieldName could be null/empty");
        }

        Field field = clazz.getDeclaredField(fieldName);
        Object obj = field.get(null);
        if (!NamedType.class.isInstance(obj)) {
            throw new Exception(clazz.getName() + "." + fieldName + " is not of " + NamedType.class.getName());
        }

        NamedType<?> namedType = (NamedType<?>)obj;
        if (!baseType.isAssignableFrom(namedType.type())) {
            throw new Exception(clazz.getName() + "." + fieldName + ".type is not of " + baseType.getName());
        }

        return (NamedType<BT>)namedType;
    }

    /**
     * Load classes and extract NamedType with generic type BT from declared static final field of each loaded class
     * @param classNameList
     *          - list of full qualified class names, to be loaded
     * @param baseType
     *          - base type
     * @param fieldName
     *          - name of static final field, which is expected to be NamedType with generic type BT
     * @param tolerateIndividualException
     *          - whether to tolerate exception when handling class at inidividual level
     * @param <BT>
     *          - generic form of base type
     * @return
     *          a {@link NamedTypes} with generic type BT
     * @throws Exception
     *          throws exception if any occurs
     *
     */
    public static <BT> NamedTypes<BT> loadClassesAndExtractNamedTypes(
            List<String> classNameList,
            Class<BT> baseType,
            String fieldName,
            boolean tolerateIndividualException) throws Exception {

        NamedTypes<BT> namedTypes = new NamedTypes<>();

        if (classNameList == null || classNameList.isEmpty()) {
            return namedTypes;
        }

        for (String className : classNameList) {
            try {
                Class<?> clazz = Class.forName(className);
                NamedType<BT> namedType = extractNamedType(clazz, fieldName, baseType);
                namedTypes.include(namedType);
            } catch (Exception e) {
                if (tolerateIndividualException) {
                    LOGGER.error(
                            "{}: TOLERABLE exception occurred when attempting to load {} and extract NamedType from it",
                            e,
                            className
                    );
                } else {
                    throw e;
                }
            }
        }

        return namedTypes;
    }
}
