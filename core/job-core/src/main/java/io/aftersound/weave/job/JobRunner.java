package io.aftersound.weave.job;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class JobRunner {

    protected final JMClient jmClient;
    protected final ExecutorService executorService;
    protected final Map<String, Object> config;

    protected JobRunner(JMClient jmClient, int totalSlots, Map<String, Object> config) {
        this.jmClient = jmClient;
        this.executorService = Executors.newFixedThreadPool(totalSlots);
        this.config = config;
    }

    public abstract void run(JobRequest jobRequest);
}
