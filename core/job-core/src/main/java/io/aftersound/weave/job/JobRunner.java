package io.aftersound.weave.job;

import io.aftersound.component.ComponentRepository;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Conceptual entity which runs requested job
 */
public abstract class JobRunner {

    /**
     * the {@link ComponentRepository} holds runtime dependencies required by this job runner
     */
    protected final ComponentRepository componentRepository;

    /**
     * Job Manager client, which this job runner uses to interact with connected job manager
     */
    protected final JMClient jmClient;

    /**
     * Thread pool which runs the job requested from job manager
     */
    protected final ExecutorService executorService;

    /**
     * Configuration for this job runner
     */
    protected final Map<String, Object> config;

    protected JobRunner(ComponentRepository componentRepository, JMClient jmClient, int totalSlots, Map<String, Object> config) {
        this.componentRepository = componentRepository;
        this.jmClient = jmClient;
        this.executorService = Executors.newFixedThreadPool(totalSlots);
        this.config = config;
    }

    /**
     * Get required dependency from {@link ComponentRepository}
     *
     * @param id id or name of the dependency
     * @param type expected type of the dependency
     * @return the dependency if it's available
     * @param <T> generic type of desired dependency
     */
    protected <T> T getRequiredDependency(String id, Class<T> type) {
        T dependency = componentRepository.getComponent(id, type);
        if (dependency == null) {
            throw new RuntimeException(
                    String.format(
                            "Runtime dependency '%s' of type '%s' is not satisfied",
                            id,
                            type.getName()
                    )
            );
        }
        return dependency;
    }

    public abstract void run(JobRequest jobRequest);
}
