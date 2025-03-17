package io.aftersound.mvel2;

import io.aftersound.util.Handle;
import org.mvel2.templates.CompiledTemplate;

import java.util.Collections;
import java.util.Map;

/**
 * A wrapper of {@link Map} between raw MVEL2 string template and {@link CompiledTemplate},
 * which provides easy access to {@link CompiledTemplate}
 */
public class CompiledTemplates {

    /**
     * Default instance of {@link CompiledTemplateRegistry}, which manages
     * {@link CompiledTemplates} in according to raw MVEL2 string template.
     * Application can still create instances of {@link CompiledTemplateRegistry}
     * if DefaultInstance cannot fulfill the need.
     */
    public static final Handle<CompiledTemplateRegistry> REGISTRY = Handle.of(
            "DefaultInstance",
            CompiledTemplateRegistry.class
    ).setAndLock(new CompiledTemplateRegistry());

    private final Map<String, CompiledTemplate> compiledTemplates;

    /**
     * Make the constructor only available to {@link CompiledTemplateRegistry}
     * @param compiledTemplates
     *          a map of raw MVEL2 string template and {@link CompiledTemplate}
     */
    CompiledTemplates(Map<String, CompiledTemplate> compiledTemplates) {
        if (compiledTemplates != null) {
            this.compiledTemplates = Collections.unmodifiableMap(compiledTemplates);
        } else {
            this.compiledTemplates = Collections.emptyMap();
        }
    }

    /**
     * get {@link CompiledTemplate} compiled from raw template
     * @param rawTemplate
     *          a raw MVEL2 string template
     * @return
     *          {@link CompiledTemplate} compiled from raw template
     */
    public CompiledTemplate get(String rawTemplate) {
        return compiledTemplates.get(rawTemplate);
    }
}
