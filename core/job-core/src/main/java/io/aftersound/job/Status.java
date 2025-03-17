package io.aftersound.job;

/**
 * Job Status Code
 */
public enum Status {
    CREATED,    // job is newly created
    ASSIGNED,   // job is assigned with a runner is assigned to run the job
    ACCEPTED,   // job is accepted by the assigned runner
    RUNNING,    // job is running
    FINISHED,   // job is finished
    FAILED,     // job is failed
    KILLED      // job is killed or cancelled or terminated
}
