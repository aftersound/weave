package io.aftersound.weave.mvel2;

import org.junit.Test;
import org.mvel2.templates.CompiledTemplate;

import java.util.Arrays;

import static org.junit.Assert.*;

public class CompiledTemplatesTest {

    @Test
    public void testCompiledTemplatesAndRegistry() {
        CompiledTemplateRegistry registry = CompiledTemplates.REGISTRY.get();
        CompiledTemplate compiledTemplate = registry.getCompiledTemplate("Hello, @{name}");
        assertNotNull(compiledTemplate);
        assertSame(compiledTemplate, registry.getCompiledTemplate("Hello, @{name}"));

        CompiledTemplates compiledTemplates = new CompiledTemplates(null);
        assertNull(compiledTemplates.get("Hello, @{name}"));

        compiledTemplates = registry.getCompiledTemplates(
                Arrays.asList(
                      "Hello, @{name}",
                      "Aloha, @{name}"
                )
        );
        assertNotNull(compiledTemplates.get("Hello, @{name}"));
        assertSame(compiledTemplate, compiledTemplates.get("Hello, @{name}"));
        assertNotNull(compiledTemplates.get("Aloha, @{name}"));
        assertNull(compiledTemplates.get("Hola, @{name}"));
    }
}