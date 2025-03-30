package io.aftersound.func.groovy;

import io.aftersound.func.*;
import io.aftersound.util.Handle;
import io.aftersound.util.ResourceRegistry;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GroovyFuncFactoryTest {

    @Test
    public void getFuncDescriptors() throws Exception {
        assertEquals(2, new GroovyFuncFactory().getFuncDescriptors().size());
    }

    @Test
    public void eval1Func1() {
        MasterFuncFactory masterFuncFactory = new MasterFuncFactory(new GroovyFuncFactory());

        // Exception case: no inputName
        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create("GROOVY:EVAL1()")
        );

        final String resourceRegistryId = UUID.randomUUID().toString();
        String funcSpec = String.format("GROOVY:EVAL1(input,greet.groovy,%s)", resourceRegistryId);

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

        assertEquals("user.getFirstName%28%29", URLEncoder.encode("user.getFirstName()", StandardCharsets.UTF_8));
    }

    @Test
    public void eval1Func2() {
        MasterFuncFactory masterFuncFactory = new MasterFuncFactory(new GroovyFuncFactory());

        Func<Object, Object> func = masterFuncFactory.create("GROOVY:EVAL1(name)");
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
    public void eval1Func3() {
        MasterFuncFactory masterFuncFactory = new MasterFuncFactory(new GroovyFuncFactory());

        Directive directive = Directive.of("t", "TRANSFORM", "GROOVY:EVAL1(name)");
        directive.setResources(Map.of("script", "def greet(name) { return \"Hello, $name!\" }\ngreet(name)\n"));

        directive.init(masterFuncFactory);

        assertEquals("Hello, world!", directive.function().apply("world"));
    }

}