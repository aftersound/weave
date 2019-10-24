package io.aftersound.weave.service.couchbase;

import io.aftersound.weave.service.ServiceContext;
import io.aftersound.weave.service.message.Messages;
import io.aftersound.weave.service.request.ParamValueHolder;
import io.aftersound.weave.service.request.ParamValueHolders;
import io.aftersound.weave.service.resources.ManagedResources;
import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateRuntime;

import java.util.LinkedHashMap;
import java.util.Map;

abstract class Executor {

    protected ManagedResources managedResources;

    @SuppressWarnings("unchecked")
    public <E extends Executor> E managedResources(ManagedResources managedResources) {
        this.managedResources = managedResources;
        return (E)this;
    }

    public abstract Object execute(CouchbaseServiceExecutionControl executionControl, ParamValueHolders request, ServiceContext context);

    protected static String render(String template, ParamValueHolders paramValueHolders) {
        Map<String, Object> variables = new LinkedHashMap<>();
        for (ParamValueHolder pvh : paramValueHolders.all()) {
            variables.put(pvh.getParamName(), pvh.getValue());
        }

        CompiledTemplate compiledTemplate = MVEL2Interpreter.instance().parseTemplate(template);
        Object rendered = TemplateRuntime.execute(compiledTemplate, variables);
        return rendered != null ? rendered.toString() : null;
    }
}
