CREATE TABLE IF NOT EXISTS namespace
(
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255),
    owner_email VARCHAR(255),
    description VARCHAR(4096),
    attributes VARCHAR(4096),
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255),
    PRIMARY KEY (name)
);
CREATE INDEX IF NOT EXISTS idx_ns_owner ON namespace (owner);
CREATE INDEX IF NOT EXISTS inx_ns_owner_email ON namespace (owner_email);

CREATE TABLE IF NOT EXISTS namespace_history
(
    id INTEGER IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255),
    owner_email VARCHAR(255),
    description VARCHAR(4096),
    attributes VARCHAR(4096),
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255)
);
CREATE INDEX IF NOT EXISTS idx_nsh_name ON namespace_history (name);
CREATE INDEX IF NOT EXISTS idx_nsh_owner ON namespace_history (owner);
CREATE INDEX IF NOT EXISTS inx_nsh_owner_email ON namespace_history (owner_email);

CREATE TABLE IF NOT EXISTS application
(
    namespace VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255),
    owner_email VARCHAR(255),
    description VARCHAR(4096),
    attributes VARCHAR(4096),
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255),
    PRIMARY KEY (namespace,name)
);
CREATE INDEX IF NOT EXISTS idx_app_owner ON application (owner);
CREATE INDEX IF NOT EXISTS inx_app_owner_email ON application (owner_email);

CREATE TABLE IF NOT EXISTS application_history
(
    id INTEGER IDENTITY PRIMARY KEY,
    namespace VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255),
    owner_email VARCHAR(255),
    description VARCHAR(4096),
    attributes VARCHAR(4096),
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255),
);
CREATE INDEX IF NOT EXISTS idx_ah_namespace ON application_history (namespace);
CREATE INDEX IF NOT EXISTS idx_ah_name ON application_history (name);
CREATE INDEX IF NOT EXISTS idx_ah_owner ON application_history (owner);
CREATE INDEX IF NOT EXISTS inx_ah_owner_email ON application_history (owner_email);

CREATE TABLE IF NOT EXISTS runtime_config
(
    k VARCHAR(512) PRIMARY KEY,
    v LONGVARBINARY,
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS runtime_config_history
(
    id INTEGER IDENTITY PRIMARY KEY,
    k VARCHAR(512) NOT NULL,
    v LONGVARBINARY,
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255)
);

CREATE INDEX IF NOT EXISTS idx_k ON runtime_config_history (k);