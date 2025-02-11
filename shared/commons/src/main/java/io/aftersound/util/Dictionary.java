package io.aftersound.util;

import io.aftersound.func.FuncFactory;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.func.common.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Dictionary {

    protected static final FuncFactory FUNC_FACTORY = new MasterFuncFactory(
            new Base64FuncFactory(),
            new BasicFuncFactory(),
            new BooleanFuncFactory(),
            new DoubleFuncFactory(),
            new IntegerFuncFactory(),
            new LongFuncFactory(),
            new MapFuncFactory(),
            new ObjectFuncFactory(),
            new ShortFuncFactory(),
            new StringFuncFactory()
    );

    /**
     * Initialize and lock the {@link Key}s declared in the dictionary class to make them immutable
     *
     * @param configKeyDictionaryClass - the implementation class of a specific Dictionary
     */
    protected static void initAndLockKeys(Class<? extends Dictionary> configKeyDictionaryClass) {
        try {
            for (Field field : configKeyDictionaryClass.getDeclaredFields()) {
                if (field.getType() == Key.class &&
                        (field.getModifiers() & Modifier.STATIC) == Modifier.STATIC &&
                        (field.getModifiers() & Modifier.FINAL) == Modifier.FINAL) {
                    field.setAccessible(true);
                    Key<?> key = (Key<?>) field.get(null);

                    if (key.parseFunc() == null) {
                        String funcSpec = String.format("MAP:GET(%s)", key.name());
                        key.bindParseFunc(FUNC_FACTORY.create(funcSpec));
                    }

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
     *
     * @param configKeyDictionaryClass - config key dictionary class
     * @param keyFilter                - filter of {@link Key} needs to be included
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
                    //noinspection unchecked
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
