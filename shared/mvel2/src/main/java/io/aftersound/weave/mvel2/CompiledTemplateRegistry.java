package io.aftersound.weave.mvel2;

import io.aftersound.weave.utils.Factory;
import io.aftersound.weave.utils.Registry;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;

import java.util.List;
import java.util.Map;

public final class CompiledTemplateRegistry extends Registry<String, CompiledTemplate> {

    public CompiledTemplate getCompiledTemplate(String rawTemplate) {
        return get(
                rawTemplate,
                new Factory<String, CompiledTemplate>() {

                    @Override
                    public CompiledTemplate create(String rawTemplate) {
                        return TemplateCompiler.compileTemplate(rawTemplate);
                    }

                }
        );
    }

    public CompiledTemplates getCompiledTemplates(List<String> rawTemplates) {
        Map<String, CompiledTemplate> compiledTemplate = get(
                rawTemplates,
                new Factory<String, CompiledTemplate>() {

                    @Override
                    public CompiledTemplate create(String rawTemplate) {
                        return TemplateCompiler.compileTemplate(rawTemplate);
                    }

                }
        );
        return new CompiledTemplates(compiledTemplate);
    }

}
