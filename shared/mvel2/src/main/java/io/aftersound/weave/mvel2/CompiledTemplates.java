package io.aftersound.weave.mvel2;

import io.aftersound.weave.utils.Handle;
import org.mvel2.templates.CompiledTemplate;

import java.util.Collections;
import java.util.Map;

public class CompiledTemplates {

    public static final Handle<CompiledTemplateRegistry> REGISTRY = Handle.of(
            "DefaultInstance",
            CompiledTemplateRegistry.class
    ).setAndLock(new CompiledTemplateRegistry());

    private final Map<String, CompiledTemplate> compiledTemplates;

    public CompiledTemplates(Map<String, CompiledTemplate> compiledTemplates) {
        if (compiledTemplates != null) {
            this.compiledTemplates = Collections.unmodifiableMap(compiledTemplates);
        } else {
            this.compiledTemplates = Collections.emptyMap();
        }
    }

    public CompiledTemplate get(String rawTemplate) {
        return compiledTemplates.get(rawTemplate);
    }
}
