package io.aftersound.func;

import io.aftersound.dict.Dictionary;
import io.aftersound.func.common.IntegerFuncFactory;
import io.aftersound.func.common.MapFuncFactory;
import io.aftersound.func.common.StringFuncFactory;
import io.aftersound.schema.Schema;
import io.aftersound.schema.SchemaHelper;
import io.aftersound.util.Handle;
import io.aftersound.util.ResourceRegistry;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MasterFuncFactoryTest {

    @Test
    public void testOf() throws Exception {
        assertNotNull(
                MasterFuncFactory.of(
                        "io.aftersound.func.common.StringFuncFactory",
                        "io.aftersound.func.common.MapFuncFactory",
                        "io.aftersound.func.common.IntegerFuncFactory"

                )
        );

        assertThrows(
                Exception.class,
                () -> MasterFuncFactory.of("io.aftersound.func.common.NonExistingFuncFactory")
        );
    }

    @Test
    public void getFuncDescriptors() {
        MasterFuncFactory funcFactory = new MasterFuncFactory(
                new StringFuncFactory(),
                new IntegerFuncFactory()
        );
        assertNotNull(funcFactory.getFuncDescriptors());

        Dictionary<Descriptor> descriptors = funcFactory.funcDescriptors();
        assertNull(descriptors.getAttribute("STR", "name"));
    }

    @Test
    public void getFuncDescriptor() {
        MasterFuncFactory funcFactory = new MasterFuncFactory(
                new StringFuncFactory(),
                new MapFuncFactory()
        );
        assertNotNull(funcFactory.getFuncDescriptor("STR"));
        assertNotNull(funcFactory.getFuncDescriptor("MAP:FROM"));
        assertNull(funcFactory.getFuncDescriptor("INT:LE"));

    }

    @Test
    public void create() {
        ResourceRegistry schemaRegistry = SchemaHelper.setupSchemaRegistry("sr123");
        schemaRegistry.register("Person", Schema.of("Person", List.of()));

        FuncFactory funcFactory = new MasterFuncFactory(new StringFuncFactory(), new MapFuncFactory());

        assertThrows(
                CreationException.class,
                () -> funcFactory.create("STR(")
        );

        assertThrows(
                CreationException.class,
                () -> funcFactory.create("")
        );

        assertThrows(
                CreationException.class,
                () -> funcFactory.create((String) null)
        );

        assertNotNull(funcFactory.create("STR(world)"));
        assertNotNull(funcFactory.create("MAP:FROM(Person,TRANSFORM,sr123)"));

        Func<Object, String> literalFunc = funcFactory.create("STR(hello)");
        assertNotNull(literalFunc);

        Func<String, Boolean> contains = funcFactory.create("STR:CONTAINS(hello)");
        assertNotNull(contains);

        assertThrows(
                CreationException.class,
                () -> funcFactory.create("STR:REVERSE()")
        );

        assertThrows(
                CreationException.class,
                () -> funcFactory.create("INT:WITHIN(1,100)")
        );
    }

    @Test
    public void testMasterAware() {
        FuncFactory funcFactory = new MasterFuncFactory(
                new StringFuncFactory(),
                new MapFuncFactory()
        );

        Func<Map<String, Object>, Boolean> func = funcFactory.create("MAP:HAS_VALUE(hobby,STR(fishing))");
        assertTrue(func.apply(Map.of("hobby", "fishing")));
    }

}