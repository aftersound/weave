package io.aftersound.func.common;

import io.aftersound.func.Func;
import io.aftersound.func.MasterFuncFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class CollectionsFuncTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void listOfFunc() {
        Object obj = masterFuncFactory.create("CHAINED(LONG:FROM(String),DATE:FROM(Long),DATE:FORMAT(yyyyMMdd),LIST:OF())")
                .apply("1615349890000");
        assertInstanceOf(List.class, obj);
        List<Object> list = (List<Object>) obj;
        assertEquals(1, list.size());
        assertEquals(1, list.size());
        assertEquals("20210309", list.get(0));
    }

    @Test
    public void listFilterFunc() {
        final List<String> list = Arrays.asList(
                "Wish",
                "Fish",
                "Which",
                "Dish",
                "Ditch"
        );

        Func<List<String>, List<String>> func = masterFuncFactory.create("LIST:FILTER(STR:END_WITH(ish))");
        List<String> result = func.apply(list);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void setOfFunc() {
        Object obj = masterFuncFactory.create("CHAINED(LONG:FROM(String),DATE:FROM(Long),DATE:FORMAT(yyyyMMdd),SET:OF())")
                .apply("1615349890000");
        assertInstanceOf(Set.class, obj);
        Set<Object> list = (Set<Object>) obj;
        assertEquals(1, list.size());
        assertEquals(1, list.size());
        assertEquals("20210309", list.iterator().next());
    }

    @Test
    public void setFilterFunc() {
        final Set<String> list = Set.of(
                "Wish",
                "Fish",
                "Which",
                "Dish",
                "Ditch"
        );

        Func<Set<String>, Set<String>> func = masterFuncFactory.create("SET:FILTER(STR:END_WITH(ish))");
        Set<String> result = func.apply(list);
        assertNotNull(result);
        assertEquals(3, result.size());
    }

}
