package io.aftersound.weave.config;

import io.aftersound.weave.common.DictionaryException;
import org.junit.Test;

import static org.junit.Assert.*;

public class DictionaryExceptionTest {

    @Test
    public void testConstructor() {
        assertNotNull(new DictionaryException(SampleDictionary.class, new Exception()).getMessage());
    }

}