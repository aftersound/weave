package io.aftersound.func.common;

import io.aftersound.func.CreationException;
import io.aftersound.func.Func;
import io.aftersound.func.MasterFuncFactory;
import io.aftersound.util.MapBuilder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("unchecked")
public class BooleanFuncFactoryTest {

    private static MasterFuncFactory masterFuncFactory;

    @BeforeAll
    public static void setup() throws Exception {
        masterFuncFactory = MasterFuncFactory.of(CommonFuncFactory.class.getName());
    }

    @Test
    public void andFunc() {
        final Map<String, Object> m = MapBuilder.<String, Object>hashMap()
                .put("firstName", "Stanley")
                .put("lastName", "Lippman")
                .put("is_elite_programmer", Boolean.TRUE)
                .put("is_male", "Y")
                .build();

        Func<Object, Boolean> f;

        f = masterFuncFactory.create("AND(MAP:GET(is_elite_programmer),MAP:HAS_VALUE(is_male,Y))");
        assertTrue(f.apply(m));

        f = masterFuncFactory.create("AND(MAP:GET(is_elite_programmer),MAP:HAS_VALUE(is_male,Y),MAP:HAS_VALUE(lastName,Kubrick))");
        assertFalse(f.apply(m));
    }

    @Test
    public void boolFromFunc() {
        assertEquals(Boolean.TRUE, masterFuncFactory.create("BOOL:FROM(Double,1.0,0.0)").apply(1.0d));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("BOOL:FROM(Double,1.0,0.0)").apply(0.0d));
        assertEquals(Boolean.TRUE, masterFuncFactory.create("BOOL:FROM(Float,1.0,0.0)").apply(1.0f));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("BOOL:FROM(Float,1.0,0.0)").apply(0.0f));
        assertEquals(Boolean.TRUE, masterFuncFactory.create("BOOL:FROM(Integer,1,0)").apply(1));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("BOOL:FROM(Integer,1,0)").apply(0));
        assertEquals(Boolean.TRUE, masterFuncFactory.create("BOOL:FROM(Long,1,0)").apply(1L));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("BOOL:FROM(Long,1,0)").apply(0L));
        assertEquals(Boolean.TRUE, masterFuncFactory.create("BOOL:FROM(Short,1,0)").apply((short)1));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("BOOL:FROM(Short,1,0)").apply((short) 0));
        assertEquals(Boolean.TRUE, masterFuncFactory.create("BOOL:FROM(String,T,F)").apply("T"));
        assertEquals(Boolean.FALSE, masterFuncFactory.create("BOOL:FROM(String,T,F)").apply("F"));

        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create("BOOL:FROM(ENUM,T,F)")
        );
    }

    @Test
    public void booleanFunc() {
        final Map<String, Object> m = MapBuilder.<String, Object>hashMap()
                .put("firstName", "Stanley")
                .put("lastName", "Tucii")
                .put("is_director", "Y")
                .put("is_choreographer", "N")
                .put("is_actor", "Y")
                .put("is_male", "Y")
                .put("oscar_winner", Boolean.FALSE)
                .build();

        Func<Object, Boolean> f = masterFuncFactory.create("AND(MAP:HAS_VALUE(firstName,Stanley),OR(MAP:HAS_VALUE(is_choreographer,Y),MAP:HAS_VALUE(is_director,Y),MAP:HAS_VALUE(is_actor,Y)),NOT(MAP:GET(oscar_winner)))");
        assertTrue(f.apply(m));
    }

    @Test
    public void booleanListFromFunc() {
        List<Boolean> values;

        values = (List<Boolean>) masterFuncFactory.create("LIST<BOOL>:FROM(Double,1.0,0.0)").apply(Arrays.asList(1.0d,0.0d,1.0d));
        assertEquals(3, values.size());
        assertEquals(Boolean.TRUE, values.get(0));
        assertEquals(Boolean.FALSE, values.get(1));
        assertEquals(Boolean.TRUE, values.get(2));

        values = (List<Boolean>) masterFuncFactory.create("LIST<BOOL>:FROM(Float,1.0,0.0)").apply(Arrays.asList(1.0f,0.0f,1.0f));
        assertEquals(3, values.size());
        assertEquals(Boolean.TRUE, values.get(0));
        assertEquals(Boolean.FALSE, values.get(1));
        assertEquals(Boolean.TRUE, values.get(2));

        values = (List<Boolean>) masterFuncFactory.create("LIST<BOOL>:FROM(Integer,2,3)").apply(Arrays.asList(3,3,2));
        assertEquals(3, values.size());
        assertEquals(Boolean.FALSE, values.get(0));
        assertEquals(Boolean.FALSE, values.get(1));
        assertEquals(Boolean.TRUE, values.get(2));

        values = (List<Boolean>) masterFuncFactory.create("LIST<BOOL>:FROM(Long,99,-99)").apply(Arrays.asList(99L,99L,99L));
        assertEquals(3, values.size());
        assertEquals(Boolean.TRUE, values.get(0));
        assertEquals(Boolean.TRUE, values.get(1));
        assertEquals(Boolean.TRUE, values.get(2));

        values = (List<Boolean>) masterFuncFactory.create("LIST<BOOL>:FROM(Short,1,-1)").apply(Arrays.asList(-1,-1,-1));
        assertEquals(3, values.size());
        assertEquals(Boolean.FALSE, values.get(0));
        assertEquals(Boolean.FALSE, values.get(1));
        assertEquals(Boolean.FALSE, values.get(2));

        values = (List<Boolean>) masterFuncFactory.create("LIST<BOOL>:FROM(String,T,F)").apply(Arrays.asList("T","T","F"));
        assertEquals(3, values.size());
        assertEquals(Boolean.TRUE, values.get(0));
        assertEquals(Boolean.TRUE, values.get(1));
        assertEquals(Boolean.FALSE, values.get(2));

        values = (List<Boolean>) masterFuncFactory.create("LIST<BOOL>:FROM(String,Y,N)").apply(Arrays.asList("Y","N","N"));
        assertEquals(3, values.size());
        assertEquals(Boolean.TRUE, values.get(0));
        assertEquals(Boolean.FALSE, values.get(1));
        assertEquals(Boolean.FALSE, values.get(2));

        values = (List<Boolean>) masterFuncFactory.create("LIST<BOOL>:FROM(String,YES,NO)").apply(Arrays.asList("YES","NO","NO"));
        assertEquals(3, values.size());
        assertEquals(Boolean.TRUE, values.get(0));
        assertEquals(Boolean.FALSE, values.get(1));
        assertEquals(Boolean.FALSE, values.get(2));


        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create("LIST<BOOL>:FROM(ENUM,T,F)")
        );
    }

    @Test
    public void equalsFunc() {
        Func<Object, Boolean> f;

        f = masterFuncFactory.create("EQ(MAP:GET(firstName),STR(Stanley))");
        final Map<String, Object> m = MapBuilder.<String, Object>hashMap()
                .put("firstName", "Stanley")
                .put("lastName", "Lippman")
                .build();
        assertTrue(f.apply(m));

        f = masterFuncFactory.create("EQ(STR(Stanley))");
        assertTrue(f.apply("Stanley"));
    }

    @Test
    public void notEqualFunc() {
        final Map<String, Object> m = MapBuilder.<String, Object>hashMap()
                .put("firstName", "Stanley")
                .put("lastName", "Lippman")
                .build();

        Func<Object, Boolean> f = masterFuncFactory.create("NE(MAP:GET(lastName),STR(Tucci))");
        assertTrue(f.apply(m));
    }

    @Test
    public void notFunc() {
        final Map<String, Object> m = MapBuilder.<String, Object>hashMap()
                .put("firstName", "Stanley")
                .put("lastName", "Lippman")
                .put("is_elite_programmer", "Y")
                .build();

        Func<Object, Boolean> f = masterFuncFactory.create("NOT(MAP:HAS_VALUE(is_elite_programmer,Y))");
        assertFalse(f.apply(m));
    }

    @Test
    public void orFunc() {
        final Map<String, Object> m = MapBuilder.<String, Object>hashMap()
                .put("firstName", "Stanley")
                .put("lastName", "Lippman")
                .put("is_elite_programmer", Boolean.TRUE)
                .put("is_male", "Y")
                .build();

        Func<Object, Boolean> f;

        f = masterFuncFactory.create("OR(MAP:GET(is_elite_programmer),MAP:HAS_VALUE(is_male,N))");
        assertTrue(f.apply(m));

        f = masterFuncFactory.create("OR(MAP:HAS_VALUE(firstName,Stanley),MAP:HAS_VALUE(lastName,Kubrick))");
        assertTrue(f.apply(m));

        f = masterFuncFactory.create("OR(MAP:HAS_VALUE(firstName,Jason),MAP:HAS_VALUE(lastName,Statham))");
        assertFalse(f.apply(m));
    }

}
