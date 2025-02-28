package io.aftersound.func.common;

import io.aftersound.func.MasterFuncFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("unchecked")
public class FileFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void fileReadFunc() {
        final String filePath = CommonValueFuncTest.class.getResource("/inventors.txt").getFile();

        List<String> inventors = (List<String>) masterFuncFactory.create("FILE:READ(IS:READ_LINES())").apply(filePath);
        assertEquals(27, inventors.size());

        byte[] bytes = (byte[]) masterFuncFactory.create("FILE:READ(IS:READ_BYTES())").apply(filePath);
        String str = new String(bytes);
        assertTrue(str.contains("Benjamin Franklin (1706-1790)"));
        assertTrue(str.contains("Jan Ernst Matzeliger (1852-1889)"));

        String str1 = (String) masterFuncFactory.create("FILE:READ(IS:READ_STRING())").apply(filePath);
        assertEquals(str, str1);
    }

}
