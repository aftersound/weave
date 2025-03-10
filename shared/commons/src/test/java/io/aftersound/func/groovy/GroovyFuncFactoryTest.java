package io.aftersound.func.groovy;

import io.aftersound.func.*;
import io.aftersound.util.Handle;
import io.aftersound.util.ResourceRegistry;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroovyFuncFactoryTest {

    @Test
    public void getFuncDescriptors() throws Exception {
        assertEquals(1, new GroovyFuncFactory().getFuncDescriptors().size());
    }

    @Test
    public void evalFunc1() {
        MasterFuncFactory masterFuncFactory = new MasterFuncFactory(new GroovyFuncFactory());

        // Exception case: no inputName
        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create("GROOVY:EVAL()")
        );

        final String resourceRegistryId = UUID.randomUUID().toString();
        String funcSpec = String.format("GROOVY:EVAL(input,greet.groovy,%s)", resourceRegistryId);

        // Exception case: no ResourceRegistry
        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create(funcSpec)
        );

        ResourceRegistry resourceRegistry = Handle.of(resourceRegistryId, ResourceRegistry.class)
                .setAndLock(new ResourceRegistry())
                .get();

        // Exception case: malformed Groovy script
        resourceRegistry.register(
                "greet.groovy",
                "def greet(name) { return \"Hello, $name!\" }\ngreet(input\n"
        );

        // Happy case
        resourceRegistry.register(
                "greet.groovy",
                "def greet(name) { return \"Hello, $name!\" }\ngreet(input)\n"
        );

        Func<Object, Object> func = masterFuncFactory.create(funcSpec);
        Object result = func.apply("world");
        assertEquals("Hello, world!", result);
    }

    @Test
    public void evalFunc2() {
        MasterFuncFactory masterFuncFactory = new MasterFuncFactory(new GroovyFuncFactory());

        Func<Object, Object> func = masterFuncFactory.create("GROOVY:EVAL(name)");
        assertThrows(
                ExecutionException.class,
                () -> func.apply("world")
        );

        if (func instanceof ResourceAware) {
            ((ResourceAware) func).bindResources(
                    Map.of("script", "def greet(name) { return \"Hello, $name!\" }\ngreet(name)\n")
            );
        }

        assertEquals("Hello, world!", func.apply("world"));
    }

    @Test
    public void evalFunc3() {
        MasterFuncFactory masterFuncFactory = new MasterFuncFactory(new GroovyFuncFactory());

        Directive directive = Directive.of("t", "TRANSFORM", "GROOVY:EVAL(name)");
        directive.setResources(Map.of("script", "def greet(name) { return \"Hello, $name!\" }\ngreet(name)\n"));

        directive.init(masterFuncFactory);

        assertEquals("Hello, world!", directive.function().apply("world"));
    }

}