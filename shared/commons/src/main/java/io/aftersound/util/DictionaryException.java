package io.aftersound.util;

public class DictionaryException extends RuntimeException {

    public DictionaryException(Class<? extends Dictionary> dictionaryClass, Throwable cause) {
        super("Key dictionary" + dictionaryClass.getName() + " cannot be initialized", cause);
    }

}
