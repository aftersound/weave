package io.aftersound.process.pipeline;

import io.aftersound.common.Context;
import io.aftersound.util.Key;
import io.aftersound.component.ComponentRepository;
import io.aftersound.process.ContextKeys;
import io.aftersound.process.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Pipeline implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Pipeline.class);

    private final Processor processor;
    private final Map<String, Object> options;
    private final ComponentRepository componentRepository;

    private final AtomicInteger status;

    private transient Config config;

    public Pipeline(Processor processor, Map<String, Object> options, ComponentRepository componentRepository) {
        this.processor = processor;
        this.options = options;
        this.componentRepository = componentRepository;

        this.status = new AtomicInteger(Status.NOT_RUNNING);

        this.config = new Config(options);
    }

    public Map<String, Object> getConfig() {
        return options;
    }

    public String getId() {
        return config.getPipelineId();
    }

    public AtomicInteger getStatus() {
        return status;
    }

    public boolean hasAllLabels(Collection<String> labels) {
        return config.getLabels().containsAll(labels);
    }

    @Override
    public void run() {
        try {
            while (status.get() == Status.RUNNING) {
                try {
                    LOGGER.info("Pipeline '{}' runs at {} in thread {}", config.getPipelineId(), System.currentTimeMillis(), Thread.currentThread().getId());
                    runPipeline(new HashMap<>(), null);
                } catch (Exception e) {
                    LOGGER.error("Exception occurred on pipeline {}: {}", config.getPipelineId(), e);
                }

                if (config.getPipelineIdleTime() > 0L) {
                    Thread.sleep(config.getPipelineIdleTime());
                }
            }
        }
        catch (InterruptedException ie) {
            LOGGER.error("Pipeline '{}' in thread {} is interrupted by {}", config.getPipelineId(), Thread.currentThread().getId(), ie);

            status.set(Status.NOT_RUNNING);

            // Restore the interrupted status
            Thread.currentThread().interrupt();
        }
    }

    public void run(Context context) {
        processor.process(context);
    }

    public Map<String, Object> runPipeline(Map<String, Object> contextVariables, List<String> outputs) {
        Context ctx = new Context();
        ctx.set(ContextKeys.COMPONENT_REPOSITORY, componentRepository);
        ctx.set(ContextKeys.VARIABLES, contextVariables);

        processor.process(ctx);

        if (outputs == null || outputs.isEmpty()) {
            return Collections.emptyMap();
        } else {
            Map<String, Object> objects = new LinkedHashMap<>();
            for (String output : outputs) {
                objects.put(output, ctx.get(Key.of(output)));
            }
            return objects;
        }
    }

    private static class Config {

        private final Map<String, Object> options;

        public Config(Map<String, Object> options) {
            this.options = options;
        }

        public String getPipelineId() {
            return (String) options.get("id");
        }

        public long getPipelineIdleTime() {
            Object v = options.get("idleTime");
            if (v instanceof Integer) {
                return ((Integer) v).longValue();
            }
            if (v instanceof Long) {
                return ((Long) v).longValue();
            }
            return 1000L;
        }

        public Collection<String> getLabels() {
            List<String> labels = (List<String>) options.get("labels");
            return labels != null ? labels : Collections.emptyList();
        }

    }

}
