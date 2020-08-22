package io.aftersound.weave.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Dictionary {

    /**
     * Lock {@link Key}s declared in the dictionary class to make them immutable
     * @param configKeyDictionaryClass
     */
    protected static void lockDictionary(Class<? extends Dictionary> configKeyDictionaryClass) {
        try {
            for (Field field : configKeyDictionaryClass.getDeclaredFields()) {
                if (field.getType() == Key.class &&
                        (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC &&
                        (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
                    field.setAccessible(true);
                    Key<?> key = (Key<?>) field.get(null);

                    // lock to make it immutable
                    key.lock();
                }
            }
        } catch (Exception e) {
            throw new DictionaryException(configKeyDictionaryClass, e);
        }
    }

    /**
     * Get declared {@link Key}s from given config key dictionary class
     * and all of its super classes whenever eligible
     * @param configKeyDictionaryClass
     *          - config key dictionary class
     * @param keyFilter
     *          - filter of {@link Key} needs to be included
     * @return list of {@link Key} which matches the filter condition
     */
    protected static List<Key<?>> getDeclaredKeys(
            Class<? extends Dictionary> configKeyDictionaryClass,
            KeyFilter keyFilter) {

        try {
            List<Key<?>> keys = new ArrayList<>();

            Class<? extends Dictionary> dictClass = configKeyDictionaryClass;
            while (dictClass != null) {
                for (Field field : dictClass.getDeclaredFields()) {
                    if (isStaticFinalKeyField(field)) {
                        field.setAccessible(true);
                        Key<?> key = (Key<?>) field.get(null);

                        if (keyFilter.isAcceptable(key)) {
                            keys.add(key);
                        }
                    }
                }

                Class<?> superClass = dictClass.getSuperclass();
                if (Dictionary.class.isAssignableFrom(superClass)) {
                    dictClass = (Class<? extends Dictionary>) superClass;
                } else {
                    dictClass = null;
                }
            }

            return Collections.unmodifiableList(keys);
        } catch (Exception e) {
            throw new DictionaryException(configKeyDictionaryClass, e);
        }
    }

    private static boolean isStaticFinalKeyField(Field field) {
        return field.getType() == Key.class &&
                (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC &&
                (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL;
    }
}
