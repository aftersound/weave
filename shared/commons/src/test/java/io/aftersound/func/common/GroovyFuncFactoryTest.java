package io.aftersound.func.common;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import io.aftersound.func.*;
import io.aftersound.util.Handle;
import io.aftersound.util.ResourceRegistry;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GroovyFuncFactoryTest {

    @Test
    public void groovyBasics() throws Exception {
        // Define the script to be executed
        String scriptText = "def greet(name) { return \"Hello, $name!\" }\n" +
                "greet(name)";

        GroovyShell shell = new GroovyShell();
        Script protoScript = shell.parse(scriptText);

        Script script = protoScript.getClass().getDeclaredConstructor().newInstance();

        Binding binding = new Binding();
        binding.setVariable("name", "world");
        script.setBinding(binding);

        Object result = script.run();

        assertEquals("Hello, world!", String.valueOf(result));
    }

    @Test
    public void evalFunc1() {
        MasterFuncFactory masterFuncFactory = new MasterFuncFactory(new GroovyFuncFactory());

        final String resourceRegistryId = UUID.randomUUID().toString();
        String funcSpec = String.format("GROOVY:EVAL(greet,%s)", resourceRegistryId);

        assertThrows(
                CreationException.class,
                () -> masterFuncFactory.create(funcSpec)
        );

        Handle.of(resourceRegistryId, ResourceRegistry.class).setAndLock(new ResourceRegistry());
        Handle.of(resourceRegistryId, ResourceRegistry.class).get()
                .register(
                        "greet",
                        "def greet(name) { return \"Hello, $name!\" }\ngreet(input)\n"
                );

        Func<Object, Object> func = masterFuncFactory.create(funcSpec);
        Object result = func.apply("world");
        assertEquals("Hello, world!", result);
    }

    @Test
    public void evalFunc2() {
        MasterFuncFactory masterFuncFactory = new MasterFuncFactory(new GroovyFuncFactory());

        Func<Object, Object> func = masterFuncFactory.create("GROOVY:EVAL()");
        assertThrows(
                ExecutionException.class,
                () -> func.apply("world")
        );

        if (func instanceof ResourceAware) {
            ((ResourceAware) func).bindResources(Map.of("script", "def greet(name) { return \"Hello, $name!\" }\ngreet(input)\n"));
        }

        assertEquals("Hello, world!", func.apply("world"));
    }

    @Test
    public void evalFunc3() {
        MasterFuncFactory masterFuncFactory = new MasterFuncFactory(new GroovyFuncFactory());

        Directive directive = Directive.of("t", "TRANSFORM", "GROOVY:EVAL()");
        directive.setResources(Map.of("script", "def greet(name) { return \"Hello, $name!\" }\ngreet(input)\n"));

        directive.init(masterFuncFactory);

        assertEquals("Hello, world!", directive.function().apply("world"));
    }

}