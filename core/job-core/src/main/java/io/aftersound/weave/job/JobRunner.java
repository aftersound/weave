package io.aftersound.weave.job;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class JobRunner {

    protected final ExecutorService executorService;
    protected final Map<String, Object> config;

    protected JobRunner(int totalSlots, Map<String, Object> config) {
        this.executorService = Executors.newFixedThreadPool(totalSlots);
        this.config = config;
    }

    public abstract void run(Map<String, Object> jobRequest);
}
