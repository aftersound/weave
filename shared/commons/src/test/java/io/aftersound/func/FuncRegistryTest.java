package io.aftersound.func;

import io.aftersound.func.common.IntegerFuncFactory;
import io.aftersound.func.common.MapFuncFactory;
import io.aftersound.func.common.StringFuncFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncRegistryTest {

    @Test
    public void create() {
        FuncFactory funcFactory = new MasterFuncFactory(
                new IntegerFuncFactory(),
                new StringFuncFactory(),
                new MapFuncFactory()
        );

        FuncRegistry funcRegistry = new FuncRegistry(funcFactory::create);

        Func<Object, String> f1 = funcRegistry.getFunc("STR(hello)");
        assertNotNull(f1);
        assertSame(f1, funcRegistry.getFunc("STR(hello)"));

        assertNotNull(funcRegistry.getFunc("MAP:FROM(Person,sr123)"));
    }

}