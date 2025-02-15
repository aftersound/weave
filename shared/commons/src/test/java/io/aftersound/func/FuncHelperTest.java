package io.aftersound.func;

import io.aftersound.func.common.*;
import io.aftersound.util.Handle;
import io.aftersound.util.ResourceRegistry;
import io.aftersound.util.TreeNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FuncHelperTest {

    @Test
    public void parseAndValidate() {
        assertThrows(
                CreationException.class,
                () -> FuncHelper.parseAndValidate(null, "STR")
        );

        assertThrows(
                CreationException.class,
                () -> FuncHelper.parseAndValidate("", "STR")
        );

        assertThrows(
                CreationException.class,
                () -> FuncHelper.parseAndValidate("STR(", "STR")
        );

        assertThrows(
                CreationException.class,
                () -> FuncHelper.parseAndValidate("STR0()", "STR")
        );

        assertNotNull(FuncHelper.parseAndValidate("STR()", "STR"));
    }

    @Test
    public void getRequiredDependency() {
        assertThrows(
                IllegalStateException.class,
                () -> FuncHelper.getRequiredDependency("default-schema-registry", ResourceRegistry.class)
        );

        Handle.of("default-schema-registry", ResourceRegistry.class).setAndLock(new ResourceRegistry());
        assertNotNull(FuncHelper.getRequiredDependency("default-schema-registry", ResourceRegistry.class));
    }

    @Test
    public void testAssertNotNull() {
        FuncHelper.assertNotNull("jaloid", "'%s' cannot be null", "name");

        assertThrows(
                IllegalArgumentException.class,
                () -> FuncHelper.assertNotNull(null, "'%s' cannot be null", "name")
        );

        Exception e = null;
        try {
            FuncHelper.assertNotNull(null, "'%s' cannot be null", "name");
        } catch (Exception ex) {
            e = ex;
        }
        assertNotNull(e);
        assertInstanceOf(IllegalArgumentException.class, e);
        assertEquals("'name' cannot be null", e.getMessage());
    }

    @Test
    public void createCreationException() throws Exception{
        CreationException ce = FuncHelper.createCreationException(TreeNode.from("STR()"), "STR(literal)", "STR(hello)");
        assertNotNull(ce);
        assertEquals("'STR()' is invalid, expecting 'STR(literal)' like 'STR(hello)'", ce.getMessage());
    }

    @Test
    public void createCreationException1() throws Exception{
        CreationException ce = FuncHelper.createCreationException(
                TreeNode.from("INT(ten)"),
                "INT(number literal)",
                "INT(10)",
                new NumberFormatException("'ten' cannot be parsed as int")
        );
        assertNotNull(ce);
        assertEquals("'INT(ten)' is invalid, expecting 'INT(number literal)' like 'INT(10)'", ce.getMessage());
        assertInstanceOf(NumberFormatException.class, ce.getCause());
    }

    @Test
    public void createParseFunc() {
        FuncFactory funcFactory = new MasterFuncFactory(
                new BasicFuncFactory(),
                new BooleanFuncFactory(),
                new DoubleFuncFactory(),
                new FloatFuncFactory(),
                new IntegerFuncFactory(),
                new LongFuncFactory(),
                new ShortFuncFactory(),
                new StringFuncFactory()
        );

        assertNotNull(FuncHelper.createParseFunc("boolean", funcFactory));
        assertNotNull(FuncHelper.createParseFunc("double", funcFactory));
        assertNotNull(FuncHelper.createParseFunc("fLOAT", funcFactory));
        assertNotNull(FuncHelper.createParseFunc("Integer", funcFactory));
        assertNotNull(FuncHelper.createParseFunc("LONG", funcFactory));
        assertNotNull(FuncHelper.createParseFunc("short", funcFactory));
        assertNotNull(FuncHelper.createParseFunc("string", funcFactory));

    }
}