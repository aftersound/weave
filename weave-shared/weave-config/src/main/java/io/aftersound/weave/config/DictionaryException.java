package io.aftersound.weave.config;

public class DictionaryException extends RuntimeException {

    public DictionaryException(Class<? extends Dictionary> dictionaryClass, Throwable cause) {
        super("Config key dictionary" + dictionaryClass.getName() + " cannot be initialized", cause);
    }

}
