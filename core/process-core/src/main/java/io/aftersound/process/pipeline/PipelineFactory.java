package io.aftersound.process.pipeline;

import io.aftersound.util.ExprTreeParsingException;
import io.aftersound.util.TreeNode;
import io.aftersound.actor.ActorRegistry;
import io.aftersound.component.ComponentRepository;
import io.aftersound.process.Processor;
import io.aftersound.process.ProcessorCreator;
import io.aftersound.process.ProcessorFactory;

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
            return new Pipeline(processor, options, componentRepository);
        } catch (Exception e) {
            throw new IllegalArgumentException("Exception occurred on creating Pipeline", e);
        }
    }

    public Pipeline createPipeline(String specExpr, Map<String, Object> options) {
        TreeNode pipelineSpec;
        try {
            pipelineSpec = TreeNode.from(specExpr);
        } catch (ExprTreeParsingException e) {
            throw new IllegalArgumentException("Exception occurred on creating Pipeline", e);
        }
        return createPipeline(pipelineSpec, options);
    }

}
