package io.aftersound.mvel2;

import io.aftersound.util.Registry;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;

import java.util.List;
import java.util.Map;

/**
 * A {@link Registry} which
 *  1.compiles raw MVEL2 string template
 *  2.caches {@link CompiledTemplate}
 *  3.makes {@link CompiledTemplate} available when raw template is given
 */
public final class CompiledTemplateRegistry extends Registry<String, CompiledTemplate> {

    /**
     * Get {@link CompiledTemplate} which is compiled from raw template.
     *   1.If exists, simply return cached one
     *   2.If not exists, create one, cache it then return
     * @param rawTemplate
     *          raw MVEL2 string template
     * @return
     *          {@link CompiledTemplate} which is compiled from raw template.
     */
    public CompiledTemplate getCompiledTemplate(String rawTemplate) {
        return get(
                rawTemplate,
                TemplateCompiler::compileTemplate
        );
    }

    /**
     * Get {@link CompiledTemplates} which holds all {@link CompiledTemplate}s compiled from raw templates.
     *   1.If exists, simply return cached one
     *   2.If not exists, create one, cache it then return
     * @param rawTemplates
     *          a list of raw MVEL2 string template
     * @return
     *          {@link CompiledTemplates} which holds all {@link CompiledTemplate}s compiled from raw templates.
     */
    public CompiledTemplates getCompiledTemplates(List<String> rawTemplates) {
        Map<String, CompiledTemplate> compiledTemplate = get(
                rawTemplates,
                TemplateCompiler::compileTemplate
        );
        return new CompiledTemplates(compiledTemplate);
    }

}
