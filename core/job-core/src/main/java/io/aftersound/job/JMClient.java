package io.aftersound.job;

import io.aftersound.job.runner.Heartbeat;
import io.aftersound.job.runner.HeartbeatAcknowledge;
import io.aftersound.job.runner.Instance;

import java.io.Closeable;

public interface JMClient extends Closeable {
    boolean registerJobRunner(Instance instance);
    boolean unregisterJobRunner(Instance instance);
    HeartbeatAcknowledge sendHeartbeat(Heartbeat heartbeat);
    boolean updateJobStatus(String jobId, JobStatus jobStatus);
}
