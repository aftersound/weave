package io.aftersound.process;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.util.ExprTreeParsingException;
import io.aftersound.util.TreeNode;
import io.aftersound.actor.ActorRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ProcessorCreator {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

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
            specAsTreeNode = TreeNode.from(spec);
        } catch (ExprTreeParsingException e) {
            throw new RuntimeException(e);
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
        return pf.create(MAPPER.convertValue(processorConfig, pf.getConfigType()));
    }
}
