package io.aftersound.func.common;

import io.aftersound.func.ExecutionException;
import io.aftersound.func.MasterFuncFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class DateFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void dateFormatFunc() {
        assertEquals("20210309", masterFuncFactory.create("DATE:FORMAT(yyyyMMdd)").apply(new Date(1615349890000L)));
        assertNull(masterFuncFactory.create("DATE:FORMAT(yyyyMMdd)").apply(null));
    }

    @Test
    public void dataFromFunc() {
        assertEquals(new Date(1615349890000L), masterFuncFactory.create("DATE:FROM(Long)").apply(1615349890000L));
        assertNull(masterFuncFactory.create("DATE:FROM(Long)").apply(null));
        assertEquals(new Date(1615349890000L), masterFuncFactory.create("DATE:FROM(LONG)").apply("1615349890000"));
        assertNull(masterFuncFactory.create("DATE:FROM(LONG)").apply(null));
        assertEquals(new Date(1615363200000L), masterFuncFactory.create("DATE:FROM(yyyyMMdd)").apply("20210310"));
        assertNull(masterFuncFactory.create("DATE:FROM(yyyyMMdd)").apply(null));

        assertThrows(
                IllegalArgumentException.class,
                () -> masterFuncFactory.create("DATE:FROM(ABC)").apply("ABC")
        );

        assertThrows(
                ExecutionException.class,
                () -> masterFuncFactory.create("DATE:FROM(yyyyMMdd)").apply("ABC")
        );

        assertThrows(
                ExecutionException.class,
                () -> masterFuncFactory.create("DATE:FROM(yyyyMMdd)").apply("")
        );
    }

    @Test
    public void dataListFromFunc() {
        List<Date> values;

        values = (List<Date>) masterFuncFactory.create("LIST<DATE>:FROM(Long)").apply(Arrays.asList(1615349890000L, 1615349890001L));
        assertEquals(2, values.size());
        assertEquals(new Date(1615349890000L), values.get(0));
        assertEquals(new Date(1615349890001L), values.get(1));

        values = (List<Date>) masterFuncFactory.create("LIST<DATE>:FROM(LONG)").apply(Arrays.asList("1615349890000", "1615349890001"));
        assertEquals(2, values.size());
        assertEquals(new Date(1615349890000L), values.get(0));
        assertEquals(new Date(1615349890001L), values.get(1));

        values = (List<Date>) masterFuncFactory.create("LIST<DATE>:FROM(yyyyMMdd)").apply(Arrays.asList("20210309", "20210310"));
        assertEquals(2, values.size());
        assertEquals(new Date(1615276800000L), values.get(0));
        assertEquals(new Date(1615363200000L), values.get(1));
    }
}
