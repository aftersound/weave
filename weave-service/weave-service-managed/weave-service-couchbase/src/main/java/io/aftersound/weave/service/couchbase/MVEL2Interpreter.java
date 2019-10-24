package io.aftersound.weave.service.couchbase;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;

import java.util.HashMap;
import java.util.Map;

class MVEL2Interpreter {

    private static final MVEL2Interpreter INSTANCE = new MVEL2Interpreter();

    private Map<String, CompiledTemplate> templateCompiledCache = new HashMap<>();

    public static MVEL2Interpreter instance() {
        return INSTANCE;
    }

    public CompiledTemplate parseTemplate(String template) {
        if (!templateCompiledCache.containsKey(template)) {
            synchronized (template) {
                if (!templateCompiledCache.containsKey(template)) {
                    CompiledTemplate compiledTemplate = TemplateCompiler.compileTemplate(template);
                    templateCompiledCache.put(template, compiledTemplate);
                }
            }
        }
        return templateCompiledCache.get(template);
    }

}
