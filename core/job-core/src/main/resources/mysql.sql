CREATE TABLE IF NOT EXISTS runner_instance
(
    iid VARCHAR(127) NOT NULL,
    namespace VARCHAR(255) NOT NULL,
    application VARCHAR(255) NOT NULL,
    environment VARCHAR(255),
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    labels JSON NOT NULL,
    status VARCHAR(31) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    PRIMARY KEY (iid),
    INDEX idx_ri_namespace (namespace),
    INDEX idx_ri_application (application),
    INDEX idx_ri_environment (environment),
    INDEX idx_ri_host (host)
);

CREATE TABLE IF NOT EXISTS runner_job
(
    job_id VARCHAR(127) NOT NULL,
    job_details JSON NOT NULL,
    runner VARCHAR(127),
    status VARCHAR(31) NOT NULL,
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    PRIMARY KEY (job_id),
    INDEX idx_rj_runner (runner),
    INDEX idx_rj_status (status),
    INDEX idx_rj_created (created),
    INDEX idx_rj_updated (updated)
);