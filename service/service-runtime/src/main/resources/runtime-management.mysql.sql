-- CREATE DATABASE IF NOT EXISTS weave;

-- USE weave;

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
    PRIMARY KEY (name),
    INDEX idx_ns_owner (owner),
    INDEX inx_ns_owner_email (owner_email)
);

CREATE TABLE IF NOT EXISTS namespace_history
(
    id INT NOT NULL AUTO_INCREMENT, -- auto row id
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255),
    owner_email VARCHAR(255),
    description VARCHAR(4096),
    attributes VARCHAR(4096),
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255),
    PRIMARY KEY (id),
    INDEX idx_nsh_name (name),
    INDEX idx_nsh_owner (owner),
    INDEX inx_nsh_owner_email (owner_email)
);

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
    PRIMARY KEY (namespace,name),
    INDEX idx_app_owner (owner),
    INDEX inx_app_owner_email (owner_email)
);

CREATE TABLE IF NOT EXISTS application_history
(
    id INT NOT NULL AUTO_INCREMENT, -- auto row id
    namespace VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    owner VARCHAR(255),
    owner_email VARCHAR(255),
    description VARCHAR(4096),
    attributes VARCHAR(4096),
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255),
    PRIMARY KEY (id),
    INDEX idx_ah_namespace (namespace),
    INDEX idx_ah_name (name),
    INDEX idx_ah_owner (owner),
    INDEX inx_ah_owner_email (owner_email)
);

CREATE TABLE IF NOT EXISTS runtime_config
(
    k VARCHAR(512) NOT NULL COMMENT 'key',
    v MEDIUMBLOB COMMENT 'value',
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255) COMMENT 'who does IUD',
    PRIMARY KEY (k)
);

CREATE TABLE IF NOT EXISTS runtime_config_history
(
    id INT NOT NULL AUTO_INCREMENT, -- auto row id
    k VARCHAR(512) NOT NULL COMMENT 'key',
    v MEDIUMBLOB COMMENT 'value',
    created TIMESTAMP(3) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    trace VARCHAR(255) COMMENT 'who does IUD',
    PRIMARY KEY (id),
    INDEX idx_rch_k (k)
);

CREATE TABLE IF NOT EXISTS instance
(
    iid VARCHAR(127) NOT NULL,
    namespace VARCHAR(255) NOT NULL,
    application VARCHAR(255) NOT NULL,
    environment VARCHAR(255),
    host VARCHAR(255) NOT NULL,
    port INTEGER NOT NULL,
    ipv4_address VARCHAR(255),
    ipv6_address VARCHAR(255),
    status VARCHAR(31) NOT NULL,
    updated TIMESTAMP(3) NOT NULL,
    PRIMARY KEY (iid),
    INDEX idx_ai_namespace (namespace),
    INDEX idx_ai_application (application),
    INDEX idx_ai_environment (environment),
    INDEX idx_ai_host (host),
    INDEX idx_ai_status (status)
);