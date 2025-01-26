package io.aftersound.func;

import io.aftersound.dict.Dictionary;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MasterFuncFactoryTest {

    @Test
    public void testOf() throws Exception {
        assertNotNull(
                MasterFuncFactory.of(
                        "io.aftersound.func.StringFuncFactory",
                        "io.aftersound.func.ParseFuncFactory",
                        "io.aftersound.func.IntegerFuncFactory"

                )
        );

        assertThrows(
                Exception.class,
                () -> MasterFuncFactory.of("io.aftersound.func.NonExistingFuncFactory")
        );
    }

    @Test
    public void getFuncDescriptors() {
        MasterFuncFactory funcFactory = new MasterFuncFactory(
                new StringFuncFactory(),
                new ParseFuncFactory(),
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
                new ParseFuncFactory()
        );
        assertNotNull(funcFactory.getFuncDescriptor("STR"));
        assertNotNull(funcFactory.getFuncDescriptor("RECORD:PARSE"));
        assertNull(funcFactory.getFuncDescriptor("INT:LE"));

    }

    @Test
    public void create() {
        FuncFactory funcFactory = new MasterFuncFactory(new StringFuncFactory(), new ParseFuncFactory());

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
        assertNotNull(funcFactory.create("STR:IS_NULL_OR_EMPTY()"));
        assertNotNull(funcFactory.create("RECORD:PARSE(Person,sr123)"));

        Func<Object, String> literalFunc = funcFactory.create("STR(hello)");
        assertNotNull(literalFunc);

        Func<String, Boolean> nullOrEmptyFunc = funcFactory.create("STR:IS_NULL_OR_EMPTY()");
        assertNotNull(nullOrEmptyFunc);

        assertThrows(
                CreationException.class,
                () -> funcFactory.create("STR:LENGTH()")
        );

        assertThrows(
                CreationException.class,
                () -> funcFactory.create("INT:LE(100)")
        );
    }

}