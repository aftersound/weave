package io.aftersound.weave.job;

import io.aftersound.weave.job.runner.Heartbeat;
import io.aftersound.weave.job.runner.HeartbeatAcknowledge;
import io.aftersound.weave.job.runner.Instance;

import java.io.Closeable;

public interface JMClient extends Closeable {
    boolean registerJobRunner(Instance instance);
    boolean unregisterJobRunner(Instance instance);
    HeartbeatAcknowledge sendHeartbeat(Heartbeat heartbeat);
    boolean updateJobStatus(String jobId, JobStatus jobStatus);
}
