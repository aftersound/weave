package io.aftersound.weave.process.pipeline;

import io.aftersound.weave.actor.ActorRegistry;
import io.aftersound.weave.component.ComponentRepository;
import io.aftersound.weave.process.Processor;
import io.aftersound.weave.process.ProcessorCreator;
import io.aftersound.weave.process.ProcessorFactory;
import io.aftersound.weave.utils.ExprTreeParsingException;
import io.aftersound.weave.utils.TextualExprTreeParser;
import io.aftersound.weave.utils.TreeNode;

import java.util.Map;

public class PipelineFactory {

    private final ComponentRepository componentRepository;

    public PipelineFactory(ComponentRepository componentRepository) {
        this.componentRepository = componentRepository;
    }

    public Pipeline createPipeline(TreeNode pipelineSpec, Map<String, Object> options) {
        try {
            ActorRegistry<ProcessorFactory> pfr = componentRepository.getComponent("processor.factory.registry");
            Processor processor = new ProcessorCreator(pfr).createProcessor(pipelineSpec, options);
            return new Pipeline(processor);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception occurred on creating Pipeline", e);
        }
    }

    public Pipeline createPipeline(String specExpr, Map<String, Object> options) {
        TreeNode pipelineSpec;
        try {
            pipelineSpec = TextualExprTreeParser.parse(specExpr);
        } catch (ExprTreeParsingException e) {
            throw new IllegalArgumentException("Exception occurred on creating Pipeline", e);
        }
        return createPipeline(pipelineSpec, options);
    }

}
