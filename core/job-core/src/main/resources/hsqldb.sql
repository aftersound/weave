SET DATABASE SQL SYNTAX MYS TRUE;

CREATE TYPE JSON AS TEXT;

CREATE FUNCTION JSON_EXTRACT(v VARCHAR(512), jsonpath VARCHAR(512)) RETURNS VARCHAR(512)
   LANGUAGE JAVA DETERMINISTIC NO SQL
   EXTERNAL NAME 'CLASSPATH:io.aftersound.weave.jdbc.Functions.jsonExtract';

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
    PRIMARY KEY (iid)
);
CREATE INDEX IF NOT EXISTS idx_ri_namespace ON runner_instance (namespace);
CREATE INDEX IF NOT EXISTS idx_ri_application ON runner_instance (application);
CREATE INDEX IF NOT EXISTS idx_ri_environment ON runner_instance (environment);
CREATE INDEX IF NOT EXISTS idx_ri_host ON runner_instance (host);

CREATE TABLE IF NOT EXISTS runner_job
(
    job_id VARCHAR(127) NOT NULL,
    details JSON NOT NULL,
    runner VARCHAR(127),
    status VARCHAR(31) NOT NULL,
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    PRIMARY KEY (job_id)
);
CREATE INDEX IF NOT EXISTS idx_rj_runner ON runner_job (runner);
CREATE INDEX IF NOT EXISTS idx_rj_status ON runner_job (status);
CREATE INDEX IF NOT EXISTS idx_rj_created ON runner_job (created);
CREATE INDEX IF NOT EXISTS idx_rj_updated ON runner_job (updated);