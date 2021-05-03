package io.aftersound.weave.process.hub;

import io.aftersound.weave.process.pipeline.PipelineNotExistException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ProcessHub {

    /**
     * @return config for this process hub
     */
    ProcessHubConfig getConfig();

    /**
     * Set up pipelines
     */
    void setupPipelines();

    /**
     * Tear down pipelines
     */
    void teardownPipelines();

    /**
     * @return
     *          information of pipelines in the scope
     */
    Map<String, Object> getPipelineInfos();

    /**
     * Execute the pipeline with specified id and optional variables
     * @param pipelineId
     *          id of the target pipeline
     * @param variables
     *          optional variables
     * @param outputs
     *          key names of context objects as outputs
     * @return
     *          context objects as outputs
     */
    Map<String, Object> executePipeline(String pipelineId, Map<String, String> variables, List<String> outputs) throws PipelineNotExistException;

    /**
     * start all the pipelines with specified labels.
     */
    void startPipelines(Collection<String> labels);

    /**
     * stop all the pipelines with specified lables.
     */
    void stopPipelines(Collection<String> labels);

    /**
     * Start the pipeline with specified id
     * @param pipelineId
     *          the identifier of the target pipeline
     */
    void startPipeline(String pipelineId) throws PipelineNotExistException;

    /**
     * Stop the pipeline with specified id
     * @param pipelineId
     *          the identifier of target pipeline
     */
    void stopPipeline(String pipelineId) throws PipelineNotExistException;
}
