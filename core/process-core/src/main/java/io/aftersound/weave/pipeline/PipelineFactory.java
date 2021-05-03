package io.aftersound.weave.pipeline;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.process.Processor;
import io.aftersound.weave.process.ProcessorFactory;
import io.aftersound.weave.utils.ExprTreeParsingException;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PipelineFactory {

    private final ComponentRepository componentRepository;

    public PipelineFactory(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    public Pipeline createPipeline(TreeNode pipelineSpec, Map<String, String> options) {
        try {
            assert (pipelineSpec != null);

            List<Processor> processorList = new ArrayList<>();
            for (TreeNode processSpec : pipelineSpec.getChildren()) {
                Processor processor = createProcessor(processSpec, options);
                processorList.add(processor);
            }
            return new Pipeline(processorList);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception occurred on creating Pipeline", e);
        }
    }

    public Pipeline createPipeline(String specExpr, Map<String, String> options) {
        TreeNode pipelineSpec;
        try {
            pipelineSpec = TextualExprTreeParser.parse(specExpr);
        } catch (ExprTreeParsingException e) {
            throw new IllegalArgumentException("Exception occurred on creating Pipeline", e);
        }
        return createPipeline(pipelineSpec, options);
    }

    /**
     * Create a {@link Processor} which honors given specification and options
     *
     * @param processSpec specification in form of {@link TreeNode}, or in textual expression form: [NAMESPACE:]OPERATION(PARAMETER_GROUP)
     * @param options     options which contains configuration for the {@link Processor}
     * @return a {@link Processor}
     */
    private Processor createProcessor(TreeNode processSpec, Map<String, String> options) {
        String name = processSpec.getData();
        String paramGroup = processSpec.getDataOfChildAt(0);
        ActorRegistry<ProcessorFactory> pfr = componentRepository.getComponent("processor.factory.registry");
        return pfr.get(name).create(paramGroup, options);
    }

}
