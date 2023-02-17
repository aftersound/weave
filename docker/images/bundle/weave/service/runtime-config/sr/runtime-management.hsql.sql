CREATE TABLE IF NOT EXISTS namespace
(
    id VARCHAR(255) PRIMARY KEY,
    owner VARCHAR(255),
    owner_email VARCHAR(255),
    description VARCHAR(4096),
    attributes VARCHAR(4096),
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_owner ON namespace (owner);

CREATE INDEX IF NOT EXISTS inx_owner_email ON namespace (owner_email);

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