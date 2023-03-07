DROP TABLE runner_instance;

CREATE TABLE IF NOT EXISTS runner_instance
(
    iid VARCHAR(127) NOT NULL,
    namespace VARCHAR(255) NOT NULL,
    application VARCHAR(255) NOT NULL,
    environment VARCHAR(255),
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    capability JSON NOT NULL,
    status VARCHAR(31) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    PRIMARY KEY (iid),
    INDEX idx_ri_namespace (namespace),
    INDEX idx_ri_application (application),
    INDEX idx_ri_environment (environment),
    INDEX idx_ri_host (host)
);