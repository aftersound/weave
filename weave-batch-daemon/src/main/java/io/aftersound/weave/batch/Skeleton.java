package io.aftersound.weave.batch;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.aftersound.weave.jackson.ObjectMapperBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class Skeleton implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Skeleton.class);

    private final String namespace;
    private final ObjectMapper jsonMapper;
    private final ObjectMapper yamlMapper;

    private volatile boolean jobSpecFetchOverdue;
    private volatile List<LiteJobSpec> jobSpecs;

    public Skeleton(String namespace) {
        this.namespace = namespace;
        this.jsonMapper = ObjectMapperBuilder.forJson().build();
        this.yamlMapper = ObjectMapperBuilder.forYAML().build();
    }

    @Override
    public void run() {
        while (true) {
            if (jobSpecs == null || jobSpecFetchOverdue) {
                this.jobSpecs = fetchJobSpecs();
            }

            // loop through visible JobSpec(s)
            for (LiteJobSpec jobSpec : jobSpecs) {
                if (jobSpec.isLeaderFollowers()) {
                    handleLeaderFollowersJob(jobSpec);
                } else {
                    handleSimpleJob(jobSpec);
                }
            }
        }
    }

    /**
     * Fetch job spec(s) from repository
     * @return
     */
    private List<LiteJobSpec> fetchJobSpecs() {

        return Collections.emptyList();
    }

    private void handleSimpleJob(LiteJobSpec jobSpec) {
        JobLock jobLock = tryAcquireLock(jobSpec);
        if (jobLock.isAcquired()) {
            triggerSimpleJob(jobSpec);
        }
    }

    private void handleLeaderFollowersJob(LiteJobSpec jobSpec) {
    }

    private JobLock tryAcquireLock(LiteJobSpec jobSpec) {
        // TODO
        return new JobLock();
    }

    private void triggerSimpleJob(LiteJobSpec jobSpec) {
        final File outputFile = new File("TODO" + ".output");
        final File errorFile = new File("TODO" + ".error");

        try {
            ProcessBuilder processBuilder = new ProcessBuilder()
                    .redirectOutput(outputFile)
                    .redirectError(errorFile)
                    .command("bash", "-c", createCommandLine(jobSpec));

            LOGGER.info(processBuilder.command().toString());
            LOGGER.info(processBuilder.environment().toString());

            Process p = processBuilder.start();
            p.waitFor();
        } catch (Exception e) {
            LOGGER.error("", e);
        } finally {
        }
    }

    private String createCommandLine(LiteJobSpec jobSpec) {
        // "java -cp test-weave-job-worker-fat.jar --weave.batch.appConfig=xyz.json --weave.batch.jobSpec=xyz.json"
        StringBuilder sb = new StringBuilder();
        sb.append("java").append(" ");
        sb.append("--weave.batch.appConfig=").append("xyz.json").append(" ");
        sb.append("--weave.batch.jobSpec=").append(jobSpec.getPath()).append(" ");
        return sb.toString();
    }
}
