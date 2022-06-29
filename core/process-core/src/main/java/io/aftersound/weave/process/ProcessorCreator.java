package io.aftersound.weave.process;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.utils.ExprTreeParsingException;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ProcessorCreator {

    private final ActorRegistry<ProcessorFactory> pfr;

    public ProcessorCreator(ActorRegistry<ProcessorFactory> pfr) {
        this.pfr = pfr;
    }

    /**
     * Create {@link Processor} based on given specification and configuration
     *
     * @param spec   the specification, in form of string, for the {@link Processor}
     * @param config the configuration for the {@link Processor}
     * @return a {@link Processor} based on given specification and configuration
     */
    public Processor createProcessor(String spec, Map<String, Object> config) {
        TreeNode specAsTreeNode;
        try {
            specAsTreeNode = TextualExprTreeParser.parse(spec);
        } catch (ExprTreeParsingException e) {
            specAsTreeNode = null;
        }

        return createProcessor(specAsTreeNode, config);
    }

    /**
     * Create {@link Processor} based on given specification and configuration
     *
     * @param spec   the specification, in form of {@link TreeNode} for the {@link Processor}
     * @param config the configuration for the {@link Processor}
     * @return a {@link Processor} based on given specification and configuration
     */
    public Processor createProcessor(TreeNode spec, Map<String, Object> config) {
        String typeName = spec.getData();
        if ("CHAIN".equals(typeName) || "CHAINED".equals(typeName)) {
            return createChainedProcessor(spec, config);
        } else {
            return createSimpleProcessor(spec, config);
        }
    }

    private Processor createChainedProcessor(TreeNode spec, Map<String, Object> config) {
        try {
            List<Processor> processorList = new ArrayList<>();
            for (TreeNode processSpec : spec.getChildren()) {
                Processor processor = createProcessor(processSpec, config);
                processorList.add(processor);
            }
            return new ChainedProcessor(processorList);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception occurred on creating ChainedProcessor", e);
        }
    }

    /**
     * Create a {@link Processor} which honors given specification and options
     *
     * @param processSpec specification in form of {@link TreeNode}, or in textual expression form: \<STEP_NAME\>
     * @param config     raw configuration which contains the configuration for the {@link Processor}
     * @return a {@link Processor}
     */
    private Processor createSimpleProcessor(TreeNode processSpec, Map<String, Object> config) {
        String name = processSpec.getData();
        Map<String, Object> processorConfig = (Map<String, Object>) config.get(name);
        String type = (String) processorConfig.get("type");
        ProcessorFactory pf = pfr.get(type);
        return pf.create(ConfigParser.parse(processorConfig, pf.getConfigType()));
    }
}
