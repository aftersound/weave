package io.aftersound.weave.job;

import com.fasterxml.jackson.databind.JsonNode;

import javax.sql.DataSource;
import java.util.UUID;

public class JobManager {

    private final DataSource dataSource;
    private final String table;

    public JobManager(DataSource dataSource, String table) {
        this.dataSource = dataSource;
        this.table = table;
    }

    public String createJob(JsonNode jobRequest) {
        // TODO
        return UUID.randomUUID().toString();
    }

    public String getJobStatus(String jobId) {
        return "unknown";
    }

}
