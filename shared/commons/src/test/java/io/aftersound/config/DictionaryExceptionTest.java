package io.aftersound.config;

import io.aftersound.util.DictionaryException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DictionaryExceptionTest {

    @Test
    public void testConstructor() {
        assertNotNull(new DictionaryException(SampleDictionary.class, new Exception()).getMessage());
    }

}