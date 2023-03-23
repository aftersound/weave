SET DATABASE SQL SYNTAX MYS TRUE;

CREATE TYPE JSON AS TEXT;

--CREATE FUNCTION JSON_EXTRACT(x VARCHAR, jsonpath VARCHAR)
--   RETURNS VARCHAR(1024)
--   LANGUAGE JAVA DETERMINISTIC NO SQL
--   EXTERNAL NAME 'CLASSPATH:io.aftersound.weave.jackson.Functions.extract';

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
CREATE INDEX IF NOT EXISTS idx_ri_status ON runner_instance (status);