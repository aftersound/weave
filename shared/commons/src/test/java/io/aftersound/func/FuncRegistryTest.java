package io.aftersound.func;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncRegistryTest {

    @Test
    public void create() {
        FuncFactory funcFactory = new MasterFuncFactory(
                new IntegerFuncFactory(),
                new StringFuncFactory(),
                new ParseFuncFactory()
        );

        FuncRegistry funcRegistry = new FuncRegistry(funcFactory::create);

        Func<Object, String> f1 = funcRegistry.getFunc("STR(hello)");
        assertNotNull(f1);
        assertSame(f1, funcRegistry.getFunc("STR(hello)"));

        assertNotNull(funcRegistry.getFunc("RECORD:PARSE(Person,sr123)"));
    }

}