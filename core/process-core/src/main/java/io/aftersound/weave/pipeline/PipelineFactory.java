package io.aftersound.weave.pipeline;

import io.aftersound.weave.component.ComponentConfig;
import io.aftersound.weave.component.ComponentRegistry;
import io.aftersound.weave.process.BaseDictionary;
import io.aftersound.weave.process.Processor;
import io.aftersound.weave.utils.Pair;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PipelineFactory {

    private final ComponentRegistry componentRegistry;
    public PipelineFactory(ComponentRegistry componentRegistry) {
        this.componentRegistry = componentRegistry;
    }

    public Pipeline createPipeline(String spec, Map<String, String> options) {
        try {
            TreeNode specTreeNode = TextualExprTreeParser.parse(spec);
            if (!"Pipeline".equals(specTreeNode.getData())) {
                throw new Exception();
            }

            List<Processor> processorList = new ArrayList<>();
            for (TreeNode procSpec : specTreeNode.getChildren()) {
                Processor processor = createProcessor(procSpec, options);
                processorList.add(processor);
            }
            return new Pipeline(processorList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Create a {@link Processor} which honors given specification and options
     * @param procSpec
     *          specification in form of {@link TreeNode}, or in textual expression form: NAMESPACE:OPERATION(PARAMETER_GROUP)
     * @param options
     *          options which contains configuration for the {@link Processor}
     * @return
     *          a {@link Processor}
     * @throws Exception
     *          any exception
     */
    private Processor createProcessor(TreeNode procSpec, Map<String, String> options) throws Exception {
        Pair<String, String> nsAndOp = parseNamespaceAndOperation(procSpec.getData());
        String type = nsAndOp.first();
        String op = nsAndOp.second();
        String paramGroup = procSpec.getChildren().get(0).getData();
        String processorId = type + "." + op + "." + paramGroup;

        ComponentConfig processorConfig = new ComponentConfig();
        processorConfig.setType(type);
        processorConfig.setId(processorId);
        processorConfig.setOptions(getProcessorOptions(op, paramGroup, options));

        componentRegistry.initializeComponent(processorConfig);
        return componentRegistry.getComponent(processorId);
    }

    private Pair<String, String> parseNamespaceAndOperation(String opStr) {
        int pos = opStr.indexOf(':');
        String ns = opStr.substring(0, pos);
        String op = opStr.substring(pos + 1);
        return Pair.of(ns, op);
    }

    private Map<String, String> getProcessorOptions(String op, String paramGroup, Map<String, String> options) {
        final String prefix = paramGroup + ":";
        Map<String, String> subOptions = new HashMap<>();
        subOptions.put(BaseDictionary.OP.name(), op);
        for (Map.Entry<String, String> e : options.entrySet()) {
            if (e.getKey().startsWith(prefix)) {
                subOptions.put(e.getKey().substring(prefix.length()), e.getValue());
            }
        }
        return subOptions;
    }

}
